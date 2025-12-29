package com.ecommerce.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.orderservice.model.Order;






public interface IOrderServiceRepo extends  JpaRepository<Order,Long> {
	
	List<Order> findByUserId(Long userId);
	@Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i  WHERE o.userId = :userId  AND i.productCode = :productCode AND o.status = 'COMPLETED'")
	boolean existsByUserIdAndItemsProductCode(Long userId, String productCode);


}
