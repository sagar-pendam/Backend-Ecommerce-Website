package com.ecommerce.orderservice.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.events.RefundEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;


@Service
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Autowired
    private IOrderServiceMgmt orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "inventory-response-events",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleInventoryResponse(InventoryEvent event) {

        log.info("Received InventoryEvent: orderId={}, product={}, status={}",
                event.getOrderId(), event.getProductCode(), event.getStatus());

        Order order = orderService.findOrderById(event.getOrderId());
        if (order == null) {
            log.error("Order not found for ID {}", event.getOrderId());
            return;
        }

        boolean allResponded = true;
        boolean anyOutOfStock = false;

        for (OrderItem item : order.getItems()) {

            if (item.getProductCode().equals(event.getProductCode())) {
                log.debug("Updating status of product {} to {}", item.getProductCode(), event.getStatus());
                item.setStatus(event.getStatus());
            }

            if (item.getStatus() == null || item.getStatus().equalsIgnoreCase("PENDING")) {
                allResponded = false;
            }

            if ("OUT_OF_STOCK".equalsIgnoreCase(item.getStatus())) {
                anyOutOfStock = true;
            }
        }

        if (anyOutOfStock) {
            order.setStatus("CANCELLED");
            log.warn("Order {} marked CANCELLED (one or more items out of stock)", order.getId());
        }

        if (allResponded) {
            if (anyOutOfStock) {

                double refundAmount = order.getTotalAmount();
                RefundEvent refundEvent = new RefundEvent(order.getId(), refundAmount);
                kafkaTemplate.send("refund-events", refundEvent);

                log.info("RefundEvent published: order={}, amount={}", order.getId(), refundAmount);

            } else {
                order.setStatus("COMPLETED");
                log.info("Order {} completed successfully", order.getId());
            }
        }

        orderService.updateOrder(order);
        log.info("Order {} updated to status {}", order.getId(), order.getStatus());
    }
}