package com.ecommerce.reviewservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.reviewservice.client.OrderClient;
import com.ecommerce.reviewservice.model.Review;
import com.ecommerce.reviewservice.repository.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private OrderClient orderClient;

    @Override
    public List<Review> getReviewsByProduct(String productCode) {
        logger.info("Fetching reviews for productCode={}", productCode);
        return reviewRepo.findByProductCode(productCode);
    }

    @Override
    public List<Review> getReviewsByUser(Long userId) {
        logger.info("Fetching reviews for userId={}", userId);
        return reviewRepo.findByUserId(userId);
    }

    @Override
    public Review addReview(Review review) {
        logger.info("Attempting to add review for userId={}, productCode={}",
                review.getUserId(), review.getProductCode());

        boolean purchased = orderClient.hasPurchased(review.getUserId(), review.getProductCode());
        if (!purchased) {
            logger.warn("User {} attempted to review product {} without purchasing.",
                    review.getUserId(), review.getProductCode());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must purchase this product to write a review");
        }

        reviewRepo.findByUserIdAndProductCode(review.getUserId(), review.getProductCode())
                .ifPresent(r -> {
                    logger.warn("Duplicate review detected for userId={}, productCode={}",
                            review.getUserId(), review.getProductCode());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review already exists for this product");
                });

        review.setCreatedAt(LocalDateTime.now());
        Review saved = reviewRepo.save(review);
        logger.info("Review added successfully: id={}", saved.getId());
        return saved;
    }

    @Override
    public Review getReviewByUserAndProduct(Long userId, String productCode) {
        logger.info("Fetching single review for userId={}, productCode={}", userId, productCode);
        return reviewRepo.findByUserIdAndProductCode(userId, productCode).orElse(null);
    }

    @Override
    public Review updateReview(Long id, Review updatedReview) {
        logger.info("Updating review id={}", id);

        Review existing = reviewRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        existing.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepo.save(existing);
        logger.info("Review updated successfully: id={}", saved.getId());
        return saved;
    }

    @Override
    public void deleteReview(Long id) {
        logger.info("Deleting review id={}", id);

        Review review = reviewRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        reviewRepo.delete(review);
        logger.info("Review deleted successfully: id={}", id);
    }
}
