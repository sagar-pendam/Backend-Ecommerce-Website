package com.ecommerce.productservice.respository;

import java.util.Optional;

import com.ecommerce.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductCode(String productCode);
}
