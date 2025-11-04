package com.ecommerce.orderservice.service;


import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.repository.IOrderServiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class IOrderServiceMgmtImple implements IOrderServiceMgmt {
    @Autowired
    private IOrderServiceRepo OrderRepo;
    @Override
    public Order createOrder(Order order) {
        Order newOrder = OrderRepo.save(order);
        return newOrder;
    }

    @Override
    public Order findOrderById(Long id) {

        return OrderRepo.findById(id).orElseThrow(()->new IllegalArgumentException("Order Not Found"));
    }

    @Override
    public List<Order> findAllOrders() {

        return OrderRepo.findAll();
    }

	@Override
	public Order updateOrder(Order order) {
		// TODO Auto-generated method stub
		 // Make sure order exists
	    Order existingOrder = OrderRepo.findById(order.getId())
	                           .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
	    existingOrder.setStatus(order.getStatus());
	    existingOrder.setQuantity(order.getQuantity());
	    existingOrder.setAmount(order.getAmount());
	    return OrderRepo.save(existingOrder);
	}
    
	@Override
	public List<Order> findOrdersByUserId(Long userId) {
	    return OrderRepo.findByUserId(userId);
	}


}
