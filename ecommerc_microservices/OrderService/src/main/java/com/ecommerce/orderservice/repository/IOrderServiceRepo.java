package com.ecommerce.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.orderservice.model.Order;






public interface IOrderServiceRepo extends  JpaRepository<Order,Long> {
	
	List<Order> findByUserId(Long userId);

}
