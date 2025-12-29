package com.ecommerce.favoriteservice.rest;

import com.ecommerce.favoriteservice.model.Favorite;
import com.ecommerce.favoriteservice.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite-api")
public class FavoriteController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    private FavoriteService service;

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        logger.info("API Call → Add Favorite: {}", favorite);

        try {
            Favorite saved = service.addFavorite(favorite);
            logger.info("Favorite added successfully: {}", saved);
            return ResponseEntity.ok(Map.of("status", "success", "data", saved));
        } catch (RuntimeException e) {
            logger.error("Error adding favorite: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable("userId") Long userId) {
        logger.info("API Call → Get Favorites for userId={}", userId);
        return ResponseEntity.ok(service.getFavoritesByUser(userId));
    }

    @Transactional
    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        logger.info("API Call → Remove Favorite: userId={}, productId={}", userId, productId);

        try {
            service.removeFavorite(userId, productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Removed from favorites"));
        } catch (RuntimeException e) {
            logger.error("Error removing favorite: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
