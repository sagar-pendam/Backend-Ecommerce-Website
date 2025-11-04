package com.ecommerce.reviewservice.service;

import com.ecommerce.reviewservice.model.Review;
import com.ecommerce.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Override
    public Review addReview(Review review) {
        return reviewRepo.save(review);
    }

    @Override
    public List<Review> getReviewsByProduct(String productCode) {
        return reviewRepo.findByProductCode(productCode);
    }

    @Override
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepo.findByUserId(userId);
    }
}
