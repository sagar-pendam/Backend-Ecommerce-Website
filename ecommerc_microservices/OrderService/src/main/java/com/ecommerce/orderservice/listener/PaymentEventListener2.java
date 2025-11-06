//package com.ecommerce.orderservice.listener;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import com.ecommerce.events.InventoryEvent;
//import com.ecommerce.orderservice.model.Order;
//import com.ecommerce.orderservice.service.IOrderServiceMgmt;
//
////@Service
//public class PaymentEventListener2 {
//
//    @Autowired
//    private IOrderServiceMgmt orderService;
//
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//
//    @KafkaListener(topics = "payment-events", groupId = "order-group")
//    public void handlePaymentEvents(PaymentEvent event) {
//        System.out.println("📩 Received PaymentEvent: " + event);
//
//        // Fetch the order
//        Order order = orderService.findOrderById(event.getOrderId());
//
//        if ("FAILED".equals(event.getStatus())) {
//            order.setStatus("CANCELLED");   // ✅ plain String
//            orderService.updateOrder(order);
//            System.out.println("❌ Payment failed, order " + order.getId() + " cancelled.");
//        } 
//        else if ("SUCCESS".equals(event.getStatus())) {
//            order.setStatus("PENDING");    // ✅ plain String (ready for inventory)
//            orderService.createOrder(order);
//
////            System.out.println("✅ Payment successful, moving to inventory reservation.");
//
//            // Publish InventoryEvent
//            InventoryEvent inventoryEvent = new InventoryEvent(
//                order.getId(),
//                order.getProductCode(),
//                order.getQuantity(),
//                "RESERVE"   // ✅ plain String
//            );
//
//            kafkaTemplate.send("inventory-events", inventoryEvent);
//        }
//    }
//}
