package com.ecommerce.orderservice.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.orderservice.client.InventoryClient;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;

@RestController
@RequestMapping("/order-api")
public class OrderOperationController {
    
	
    @Autowired
    private IOrderServiceMgmt orderService;
    
    @Autowired
    private InventoryClient inventoryClient;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


//    @PostMapping("/create-order")
//    public ResponseEntity<String> createOrder(@RequestBody Order order) {
//        
//        order.setStatus("PENDING");
//        Order createdOrder = orderService.createOrder(order);
//
//        // Publish ORDER_CREATED event to inventory
//        OrderEvent orderEvent = new OrderEvent(
//            createdOrder.getId(),
//            createdOrder.getProductCode(),
//            createdOrder.getQuantity(),
//            createdOrder.getAmount(),
//            createdOrder.getStatus()   // ✅ already String
//        );
//
////        kafkaTemplate.send("order-events", orderEvent);
//        kafkaTemplate.send("order-events", orderEvent);
//
//        return new ResponseEntity<>(
//            "Order placed successfully with ID: " + createdOrder.getId() + orderEvent.getStatus(),
//            HttpStatus.CREATED
//        );
//    }
    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(
            @RequestBody Order order,
            @RequestHeader("X-User-Id") Long userId) {

        order.setUserId(userId); // ✅ ensure the order is tied to the logged-in user
        order.setStatus("PENDING");

        Order createdOrder = orderService.createOrder(order);

        // Publish ORDER_CREATED event
        OrderEvent orderEvent = new OrderEvent(
            createdOrder.getId(),
            createdOrder.getProductCode(),
            createdOrder.getQuantity(),
            createdOrder.getAmount(),
            createdOrder.getStatus()
        );

        kafkaTemplate.send("order-events", orderEvent);

        return new ResponseEntity<>(
            "Order placed successfully with ID: " + createdOrder.getId(),
            HttpStatus.CREATED
        );
    }


    @GetMapping("/show-order/{id}")
    public ResponseEntity<Order> showOrder(@PathVariable Long id) {
        Order showOrder = orderService.findOrderById(id);
        return new ResponseEntity<>(showOrder, HttpStatus.OK);
    }

    @GetMapping("/show-order/all")
    public ResponseEntity<List<Order>> showOrdersList() {
        List<Order> showOrders = orderService.findAllOrders();
        return new ResponseEntity<>(showOrders, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.findOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

}
