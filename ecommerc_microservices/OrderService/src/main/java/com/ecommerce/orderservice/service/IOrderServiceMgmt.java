package com.ecommerce.orderservice.service;

import java.util.List;
import com.ecommerce.orderservice.model.Order;

public interface IOrderServiceMgmt {
    Order createOrder(Order order);
    Order findOrderById(Long id);
    List<Order> findAllOrders();
    Order updateOrder(Order order);
    boolean deleteOrder(Long id);

    List<Order> findOrdersByUserId(Long userId);
}
