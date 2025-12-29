package com.ecommerce.paymentservice.service;


import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.events.PaymentEvent;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;


@Service
public class PaymentServiceImple implements PaymentService {
	 private static final Logger log = LoggerFactory.getLogger(PaymentServiceImple.class);
	@Value("${stripe.secret.key}")
    private String secretKey;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;


    @Override
    public PaymentResponse processPayment(Long orderId, Double amount, String paymentMethod) {
        try {
            if ("COD".equalsIgnoreCase(paymentMethod)) {
            	 log.info("Processing COD payment for order {}", orderId);
                // 1 Save payment as PENDING (since payment is on delivery)
                Payment payment = new Payment();
                payment.setOrderId(orderId);
                payment.setAmount(amount);
                payment.setTransactionId("COD-" + System.currentTimeMillis());
                payment.setStatus("PENDING");
                payment.setPaymentMethod("COD"); //  FIXED
                payment.setCreatedAt(LocalDateTime.now());
                paymentRepository.save(payment);

                // 2️ Publish COD Payment Event (so inventory/order services act)
                PaymentEvent event = new PaymentEvent(orderId, amount, "PENDING", payment.getPaymentMethod());
                kafkaTemplate.send("payment-events", event);

                System.out.println("COD PaymentEvent published for OrderID: " + orderId);
                log.info("COD PaymentEvent published for OrderID: {}", orderId);
                // 3️ Return response (no client secret needed)
                return new PaymentResponse(orderId, amount, "PENDING", "COD");
            }

            // === Stripe Flow ===
            log.info("Processing Stripe payment for order {}", orderId);
            Stripe.apiKey = secretKey;
            long amountInPaise = (long) (amount * 100);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInPaise)
                    .setCurrency("inr")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            String clientSecret = paymentIntent.getClientSecret();
            log.info("Stripe PaymentIntent created: {}", paymentIntent.getId());
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(amount);
            payment.setTransactionId(paymentIntent.getId());
            payment.setStatus("PENDING");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setPaymentMethod(paymentMethod);

            paymentRepository.save(payment);

            System.out.println(" Stripe PaymentIntent created: " + paymentIntent.getId());
            PaymentEvent event = new PaymentEvent(orderId, amount, "SUCCESS", payment.getPaymentMethod());
            kafkaTemplate.send("payment-events", event);
            log.info("PaymentEvent published for order {} via Stripe", orderId);
            return new PaymentResponse(orderId, amount, "SUCCESS", clientSecret);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Payment processing failed for order {}: {}", orderId, e.getMessage(), e);
            return new PaymentResponse(orderId, amount, "FAILED",null);
        }
    }


    @Override
    public void updatePaymentStatus(Long orderId, String status) {
    	log.info("Updating payment status for order {} → {}", orderId, status);
    	Payment payment = paymentRepository.findByOrderId(orderId)
    		    .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
            kafkaTemplate.send("payment-events", new PaymentEvent(orderId, payment.getAmount(), status,payment.getPaymentMethod()));
            System.out.println("Payment status updated: " + status);
            log.info("Payment status updated and event published for order {} → {}", orderId, status);
        }
    }
    

    public PaymentResponse getPaymentByOrder(Long orderId) {
        log.info("Fetching payment details for order {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> {
                    log.info("Payment found for order {} → status: {}", orderId, payment.getStatus());
                    return new PaymentResponse(
                            payment.getOrderId(),
                            payment.getAmount(),
                            payment.getStatus(),
                            null
                    );
                })
                .orElseGet(() -> {
                    log.warn("Payment not found for order {}", orderId);
                    return null;
                });
    }
    @Override
    public void refundPayment(Long orderId, Double amount) {
    	log.info("Initiating refund of ₹{} for order {}", amount, orderId);
        System.out.println("Refunding ₹" + amount + " for order " + orderId);

        // Find the original payment record
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        // Update payment status to REFUNDED
        payment.setStatus("REFUNDED");

        // Generate a refund transaction ID
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        log.info("Refund completed for order {} → transactionId: {}", orderId, payment.getTransactionId());
        System.out.println("Refund completed for order " + orderId + ", transactionId: " + payment.getTransactionId());
    }
    
    public void confirmStripePayment(Long orderId, String paymentIntentId) {
        try {
        	 log.info("Confirming Stripe payment for order {} with PaymentIntent {}", orderId, paymentIntentId);
            Stripe.apiKey = secretKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            String status = paymentIntent.getStatus().equals("succeeded") ? "SUCCESS" : "FAILED";

            updatePaymentStatus(orderId, status);  // this will also publish Kafka event
            log.info("Stripe payment confirmation completed for order {} → status: {}", orderId, status);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error confirming Stripe payment for order {}: {}", orderId, e.getMessage(), e);
            updatePaymentStatus(orderId, "FAILED");
        }
    }


}
