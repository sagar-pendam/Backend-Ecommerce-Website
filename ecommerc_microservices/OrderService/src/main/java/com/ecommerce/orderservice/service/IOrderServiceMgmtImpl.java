package com.ecommerce.orderservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.repository.IOrderServiceRepo;

@Service
public class IOrderServiceMgmtImpl implements IOrderServiceMgmt {
	private static final Logger log = LoggerFactory.getLogger(IOrderServiceMgmtImpl.class);
    @Autowired
    private IOrderServiceRepo orderRepo;

    @Override
    public Order createOrder(Order order) {
    	 log.info("Creating new order for user {}", order.getUserId());
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
            log.debug("Order status was null/empty. Set default status to PENDING");
        }

        // Ensure bidirectional relationship is set
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
            log.debug("Linked order item {} to order {}", item.getProductCode(), order.getId());
        }

        Order savedOrder = orderRepo.save(order);
        log.info("Order {} saved successfully for user {}", savedOrder.getId(), order.getUserId());
        return savedOrder;
    }

    @Override
    public Order findOrderById(Long id) {
    	  log.debug("Searching for order with ID {}", id);
        return orderRepo.findById(id).orElse(null);
    }


    @Override
    public List<Order> findAllOrders() {
        log.info("Fetching all orders from repository");
        List<Order> orders = orderRepo.findAll();
        log.info("Total {} orders found", orders.size());
        return orders;
    }
    @Override
    public Order updateOrder(Order order) {
    	  log.info("Updating order {}", order.getId());
        Order existingOrder = findOrderById(order.getId());
        if (existingOrder == null) {
            log.error("Order {} not found. Update failed.", order.getId());
            return null;
        }
        existingOrder.setStatus(order.getStatus());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setItems(order.getItems());
        for (OrderItem item : existingOrder.getItems()) {
            item.setOrder(existingOrder);
            log.debug("Updated order item {} for order {}", item.getProductCode(), order.getId());
        }
        Order updatedOrder = orderRepo.save(existingOrder);
        log.info("Order {} updated successfully", updatedOrder.getId());
        return updatedOrder;
    }

    @Override
    public List<Order> findOrdersByUserId(Long userId) {
        log.info("Fetching orders for user {}", userId);
        List<Order> orders = orderRepo.findByUserId(userId);
        log.info("Found {} orders for user {}", orders.size(), userId);
        return orders;
    }
    @Override
    public boolean deleteOrder(Long id) {
        log.warn("Attempting to delete order {}", id);
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
            log.info("Order {} deleted successfully", id);
            return true;
        } else {
            log.error("Order {} not found. Delete failed.", id);
            return false;
        }
    }
    
    @Override
    public Order getOrderById(Long orderId) {
    	log.debug("Fetching order by ID {}", orderId);
        return orderRepo.findById(orderId).orElse(null);
    }

    @Override
    public boolean hasPurchased(Long userId, String productCode) {
        log.info("Checking if user {} has purchased product {}", userId, productCode);
        boolean purchased = orderRepo.existsByUserIdAndItemsProductCode(userId, productCode);
        log.info("User {} purchase status for product {} → {}", userId, productCode, purchased);
        return purchased;
    }

}
