package com.ecommerce.reviewservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.reviewservice.model.Review;
import com.ecommerce.reviewservice.service.ReviewService;

@RestController
@RequestMapping("/review-api")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        logger.info("API CALL: Add review userId={}, productCode={}",
                review.getUserId(), review.getProductCode());
        Review savedReview = reviewService.addReview(review);
        logger.info("Review created with id={}", savedReview.getId());
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productCode}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable("productCode") String productCode) {
        logger.info("API CALL: Get reviews for productCode={}", productCode);
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productCode));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable("userId") Long userId) {
        logger.info("API CALL: Get reviews for userId={}", userId);
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/product/{productCode}/user/{userId}")
    public ResponseEntity<Review> getUserReviewForProduct(
            @PathVariable("productCode") String productCode,
            @PathVariable("userId") Long userId) {

        logger.info("API CALL: Get review for userId={}, productCode={}", userId, productCode);
        return ResponseEntity.ok(reviewService.getReviewByUserAndProduct(userId, productCode));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable("id") Long id,
            @RequestBody Review updatedReview) {

        logger.info("API CALL: Update review id={}", id);
        return ResponseEntity.ok(reviewService.updateReview(id, updatedReview));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        logger.info("API CALL: Delete review id={}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }
}
