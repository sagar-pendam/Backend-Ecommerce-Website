package com.ecommerce.orderservice.events;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.events.RefundEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

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
        Order order = orderService.findOrderById(event.getOrderId());

        if ("INVENTORY_CONFIRMED".equals(event.getStatus())) {
            // ✅ Inventory successful → complete order
            order.setStatus("COMPLETED");
        }
        else if ("OUT_OF_STOCK".equals(event.getStatus())) {
            // 🔴 Inventory failed → rollback: cancel order + refund payment
            order.setStatus("CANCELLED");

            RefundEvent refundEvent = new RefundEvent(order.getId(), order.getAmount());
            kafkaTemplate.send("refund-events", refundEvent);

            System.out.println("💸 RefundEvent published for order " + order.getId());
        }
        else {
            // 🟡 Unexpected → keep order pending
            order.setStatus("PENDING");
        }

        // Save updated order
        orderService.updateOrder(order);
        System.out.println("📦 Order ID " + order.getId() + " status updated to " + order.getStatus());
    }
}
