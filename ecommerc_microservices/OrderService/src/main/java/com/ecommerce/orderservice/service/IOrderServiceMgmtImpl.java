package com.ecommerce.orderservice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.repository.IOrderServiceRepo;

@Service
public class IOrderServiceMgmtImpl implements IOrderServiceMgmt {

    @Autowired
    private IOrderServiceRepo orderRepo;

    @Override
    public Order createOrder(Order order) {
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }

        // Ensure bidirectional relationship is set
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
        }

        return orderRepo.save(order);
    }

    @Override
    public Order findOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }


    @Override
    public List<Order> findAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public Order updateOrder(Order order) {
        Order existingOrder = findOrderById(order.getId());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setItems(order.getItems());
        for (OrderItem item : existingOrder.getItems()) {
            item.setOrder(existingOrder);
        }
        return orderRepo.save(existingOrder);
    }

    @Override
    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepo.findByUserId(userId);
    }
    @Override
    public boolean deleteOrder(Long id) {
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
            return true;
        }
        return false;
    }

}
