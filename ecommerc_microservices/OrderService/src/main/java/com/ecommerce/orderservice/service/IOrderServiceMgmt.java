package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.model.Order;

import java.util.List;



public interface IOrderServiceMgmt {

    public Order createOrder(Order order);
    public Order findOrderById(Long id);
    public List<Order> findAllOrders();
    public Order updateOrder(Order order);
    public List<Order> findOrdersByUserId(Long userId);
}
