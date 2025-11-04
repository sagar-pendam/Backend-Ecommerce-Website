package com.ecommerce.paymentservice.kafka;


import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.events.OrderEvent;
import com.ecommerce.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void consumeOrderEvent(OrderEvent event) {
        System.out.println("📩 Received Order Event: " + event);

        if ("PENDING".equals(event.getStatus())) {
            // process payment for new order
            PaymentResponse paymentEvent = paymentService.processPayment(event.getOrderId(), event.getAmount());
            System.out.println("💳 Payment processed: " + paymentEvent);
        }
    }

}
