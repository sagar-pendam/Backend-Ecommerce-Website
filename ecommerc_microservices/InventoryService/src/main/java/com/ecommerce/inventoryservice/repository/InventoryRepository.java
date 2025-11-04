package com.ecommerce.inventoryservice.repository;

import java.util.Optional;

import com.ecommerce.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;



public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	 Optional<Inventory> findByProductCode(String productCode);
}
