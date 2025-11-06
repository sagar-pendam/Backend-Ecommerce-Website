package com.ecommerce.orderservice.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.events.RefundEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;

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

        String status = event.getStatus();

        switch (status) {
            case "INVENTORY_CONFIRMED":
                order.setStatus("COMPLETED");
                break;

            case "OUT_OF_STOCK":
                order.setStatus("CANCELLED");

                RefundEvent refundEvent = new RefundEvent(order.getId(), order.getTotalAmount());
                kafkaTemplate.send("refund-events", refundEvent);
                System.out.println("💸 RefundEvent published for order " + order.getId());
                break;

            default:
                order.setStatus("PENDING");
                break;
        }

        orderService.updateOrder(order);
        System.out.println("📦 Order ID " + order.getId() + " status updated to " + order.getStatus());
    }
}
