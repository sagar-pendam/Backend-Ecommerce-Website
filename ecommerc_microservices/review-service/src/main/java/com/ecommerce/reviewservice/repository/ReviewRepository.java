package com.ecommerce.reviewservice.repository;

import com.ecommerce.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductCode(String productCode);
    List<Review> findByUserId(Long userId);
}
