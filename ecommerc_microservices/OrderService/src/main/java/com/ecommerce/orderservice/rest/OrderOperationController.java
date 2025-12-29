package com.ecommerce.orderservice.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.service.IOrderServiceMgmt;

@RestController
@RequestMapping("/order-api")
public class OrderOperationController {
	  private static final Logger log = LoggerFactory.getLogger(OrderOperationController.class);
    @Autowired
    private IOrderServiceMgmt orderService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Order order,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Request received to create order for userId={}", userId);

        order.setUserId(userId);
        order.setStatus("PENDING");

        double total = 0.0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                total += item.getAmount() * item.getQuantity();
            }
        }
        order.setTotalAmount(total);

        Order createdOrder = orderService.createOrder(order);

        log.info("Order created → id={}, total={}", createdOrder.getId(), createdOrder.getTotalAmount());

        OrderEvent orderEvent = new OrderEvent(
                createdOrder.getId(),
                createdOrder.getTotalAmount(),
                createdOrder.getStatus()
        );
        kafkaTemplate.send("order-events", orderEvent);

        log.debug("OrderEvent published for order {}", createdOrder.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", createdOrder.getId());
        response.put("totalAmount", createdOrder.getTotalAmount());
        response.put("status", createdOrder.getStatus());
        response.put("message", "Order placed successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/show-order/{id}")
    public ResponseEntity<Order> showOrder(@PathVariable("id") Long id) {
        log.debug("Fetching order {}", id);
        Order order = orderService.findOrderById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/show-order/all")
    public ResponseEntity<List<Order>> showOrdersList() {
        log.info("Fetching all orders");
        List<Order> orders = orderService.findAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable("userId") Long userId) {
        log.info("Fetching orders for userId={}", userId);
        List<Order> orders = orderService.findOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") Long orderId) {
        log.debug("Looking up order {}", orderId);
        Order order = orderService.getOrderById(orderId);
        return order == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(order);
    }

    @GetMapping("/verify-purchase")
    public ResponseEntity<Boolean> hasUserPurchasedProduct(
            @RequestParam Long userId,
            @RequestParam String productCode) {

        log.info("Verifying purchase: userId={}, productCode={}", userId, productCode);
        boolean purchased = orderService.hasPurchased(userId, productCode);
        return ResponseEntity.ok(purchased);
    }}
