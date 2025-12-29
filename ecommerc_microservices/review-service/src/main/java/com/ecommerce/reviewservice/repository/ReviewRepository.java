package com.ecommerce.reviewservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.reviewservice.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductCode(String productCode);
    List<Review> findByUserId(Long userId);
    Optional<Review> findByUserIdAndProductCode(Long userId, String productCode);
}
