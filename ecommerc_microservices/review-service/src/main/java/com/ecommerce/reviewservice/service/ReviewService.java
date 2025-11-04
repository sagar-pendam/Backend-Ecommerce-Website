package com.ecommerce.reviewservice.service;

import com.ecommerce.reviewservice.model.Review;
import java.util.List;

public interface ReviewService {
    Review addReview(Review review);
    List<Review> getReviewsByProduct(String productCode);
    List<Review> getReviewsByUser(Long userId);
}
