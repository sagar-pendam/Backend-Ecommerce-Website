package com.ecommerce.paymentservice.service;


import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.events.PaymentEvent;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;


@Service
public class PaymentServiceImple implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentResponse processPayment(Long orderId, Double amount) {
        // Generate a mock transaction ID
        String transactionId = "TXN" + System.currentTimeMillis() + new Random().nextInt(1000);

        // Simulate payment gateway (80% success, 20% failure)
//        boolean isSuccess = new Random().nextDouble() < 0.8;
//        boolean isSuccess = false;
        String status = amount < 1000 ? "SUCCESS" : "FAILED";

        // Save payment details to DB
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setTransactionId(transactionId);
        payment.setStatus(status);

        if (paymentRepository.existsByOrderId(orderId)) {
            Payment existing = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment exists but not found in DB"));
            return new PaymentResponse(
                    existing.getOrderId(),
                    existing.getAmount(),
                    existing.getStatus()
            );
        }


       

        // Publish Kafka event
        PaymentEvent event = new PaymentEvent(orderId, amount, status);
        kafkaTemplate.send("payment-events", event);
        System.out.println("💳 Published PaymentEvent → " + event);


        // Return response
        return new PaymentResponse(
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

    public PaymentResponse getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(payment -> new PaymentResponse(
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getStatus()))
                .orElse(null);
    }

    @Override
    public void refundPayment(Long orderId, Double amount) {
        System.out.println("✅ Refunding ₹" + amount + " for order " + orderId);

        // Find the original payment record
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        // Update payment status to REFUNDED
        payment.setStatus("REFUNDED");

        // Generate a refund transaction ID
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        System.out.println("💰 Refund completed for order " + orderId + ", transactionId: " + payment.getTransactionId());
    }

}
