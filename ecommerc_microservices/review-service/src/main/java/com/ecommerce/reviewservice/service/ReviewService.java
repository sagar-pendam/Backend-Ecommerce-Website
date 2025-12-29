package com.ecommerce.reviewservice.service;

import java.util.List;

import com.ecommerce.reviewservice.model.Review;

public interface ReviewService {

    List<Review> getReviewsByProduct(String productCode);

    List<Review> getReviewsByUser(Long userId);

    Review addReview(Review review);

    Review getReviewByUserAndProduct(Long userId, String productCode);

    Review updateReview(Long id, Review updatedReview);

    void deleteReview(Long id);
}
