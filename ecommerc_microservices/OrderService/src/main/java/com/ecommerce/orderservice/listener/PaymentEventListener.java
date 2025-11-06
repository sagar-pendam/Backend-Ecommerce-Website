package com.ecommerce.orderservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.events.PaymentEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;

@Service
public class PaymentEventListener {

    @Autowired
    private IOrderServiceMgmt orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void handlePaymentEvents(PaymentEvent event) {
        System.out.println("📩 Received PaymentEvent: " + event);

        Order order = orderService.findOrderById(event.getOrderId());

        if ("FAILED".equals(event.getStatus())) {
            order.setStatus("CANCELLED");
            orderService.updateOrder(order);
            System.out.println("❌ Payment failed, order " + order.getId() + " cancelled.");
        } else if ("SUCCESS".equals(event.getStatus())) {
            order.setStatus("PENDING");
            orderService.updateOrder(order);

            // Publish InventoryEvent for each product in order
            for (OrderItem item : order.getItems()) {
                InventoryEvent inventoryEvent = new InventoryEvent(
                        order.getId(),
                        item.getProductCode(),
                        item.getQuantity(),
                        "RESERVE"
                );
                kafkaTemplate.send("inventory-events", inventoryEvent);
                System.out.println("📦 InventoryEvent sent for " + item.getProductCode());
            }
        }
    }
}
