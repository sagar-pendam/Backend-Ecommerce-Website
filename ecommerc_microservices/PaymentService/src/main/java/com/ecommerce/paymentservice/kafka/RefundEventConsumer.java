package com.ecommerce.paymentservice.kafka;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.RefundEvent;
import com.ecommerce.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundEventConsumer {

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(
            topics = "refund-events",
            groupId = "payment-group",
            containerFactory = "refundEventKafkaListenerFactory"
    )
    public void handleRefundEvent(RefundEvent event) {
        System.out.println("💸 Received RefundEvent: " + event);

        Long orderId = event.getOrderId();
        Double amount = event.getAmount();

        System.out.println("💰 Processing refund for order: " + orderId);
        System.out.println("💵 Refund amount: " + amount);

        paymentService.refundPayment(orderId, amount);
    }

}
