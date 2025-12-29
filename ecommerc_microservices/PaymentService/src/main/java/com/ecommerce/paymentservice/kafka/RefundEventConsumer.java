package com.ecommerce.paymentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecommerce.events.RefundEvent;
import com.ecommerce.paymentservice.service.PaymentService;
@Service
public class RefundEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(RefundEventConsumer.class);

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(
            topics = "refund-events",
            groupId = "payment-group",
            containerFactory = "refundEventKafkaListenerFactory"
    )
    public void handleRefundEvent(RefundEvent event) {
        log.info("Received RefundEvent: {}", event);

        Long orderId = event.getOrderId();
        Double amount = event.getAmount();

        log.info("Triggering refund process → OrderID: {}, Amount: {}", orderId, amount);

        paymentService.refundPayment(orderId, amount);

        log.info("Refund processed for Order: {}", orderId);
    }
}
