package com.ecommerce.orderservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.events.PaymentEvent;
import com.ecommerce.events.RefundEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;

@Service
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    @Autowired
    private IOrderServiceMgmt orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvents(PaymentEvent event) {

        log.info("Received PaymentEvent: {}", event);

        Order order = orderService.findOrderById(event.getOrderId());
        if (order == null) {
            log.error("Order not found for payment event. OrderId={}", event.getOrderId());
            return;
        }

        String paymentMethod = event.getPaymentMethod();
        String status = event.getStatus();

        // CARD Payment
        if ("CARD".equalsIgnoreCase(paymentMethod)) {

            if ("FAILED".equalsIgnoreCase(status)) {
                order.setStatus("CANCELLED");
                orderService.updateOrder(order);

                double refundAmount = order.getTotalAmount();
                RefundEvent refundEvent = new RefundEvent(order.getId(), refundAmount);
                kafkaTemplate.send("refund-events", refundEvent);

                log.warn("CARD payment FAILED → order {} cancelled, refund issued {}", order.getId(), refundAmount);
                return;
            }

            if ("SUCCESS".equalsIgnoreCase(status)) {
                order.setStatus("PENDING");
                orderService.updateOrder(order);
                log.info("CARD payment SUCCESSFUL for order {}", order.getId());

                for (OrderItem item : order.getItems()) {
                    InventoryEvent inventoryEvent = new InventoryEvent(
                            order.getId(), item.getProductCode(), item.getQuantity(), "RESERVE"
                    );
                    kafkaTemplate.send("inventory-events", inventoryEvent);
                    log.debug("Sent InventoryEvent for {}", item.getProductCode());
                }
                return;
            }
        }

        // COD Payment
        if ("COD".equalsIgnoreCase(paymentMethod)) {

            log.info("COD payment → order {} accepted", order.getId());

            order.setStatus("PENDING");
            orderService.updateOrder(order);

            for (OrderItem item : order.getItems()) {
                InventoryEvent inventoryEvent = new InventoryEvent(
                        order.getId(), item.getProductCode(), item.getQuantity(), "RESERVE"
                );
                kafkaTemplate.send("inventory-events", inventoryEvent);
                log.debug("Sent InventoryEvent (COD) for {}", item.getProductCode());
            }
        }
    }
}