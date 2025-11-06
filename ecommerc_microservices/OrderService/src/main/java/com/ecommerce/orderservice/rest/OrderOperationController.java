package com.ecommerce.orderservice.rest;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-api")
public class OrderOperationController {

    @Autowired
    private IOrderServiceMgmt orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // 🟢 Create new order with multiple items
    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(
            @RequestBody Order order,
            @RequestHeader("X-User-Id") Long userId) {

        // ✅ Link order to user and set default status
        order.setUserId(userId);
        order.setStatus("PENDING");

        // ✅ Calculate total amount and link items
        double total = 0.0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                total += item.getPrice() * item.getQuantity();
            }
        }
        order.setTotalAmount(total);

        // ✅ Save order to DB (CascadeType.ALL will handle items)
        Order createdOrder = orderService.createOrder(order);

        // ✅ Publish Kafka events for each order item
        for (OrderItem item : createdOrder.getItems()) {
            OrderEvent orderEvent = new OrderEvent(
                    createdOrder.getId(),
                    item.getProductCode(),
                    item.getQuantity(),
                    item.getPrice() * item.getQuantity(),
                    createdOrder.getStatus() // "PENDING"
            );
            kafkaTemplate.send("order-events", orderEvent);
        }

        return new ResponseEntity<>(
                "Order placed successfully with ID: " + createdOrder.getId(),
                HttpStatus.CREATED
        );
    }

    // 🟢 Show single order by ID
    @GetMapping("/show-order/{id}")
    public ResponseEntity<Order> showOrder(@PathVariable Long id) {
        Order order = orderService.findOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    // 🟢 Show all orders
    @GetMapping("/show-order/all")
    public ResponseEntity<List<Order>> showOrdersList() {
        List<Order> orders = orderService.findAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // 🟢 Show all orders for a specific user
    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.findOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
