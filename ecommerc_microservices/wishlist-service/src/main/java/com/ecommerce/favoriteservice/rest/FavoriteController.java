package com.ecommerce.favoriteservice.rest;

import com.ecommerce.favoriteservice.model.Favorite;
import com.ecommerce.favoriteservice.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite-api")
public class FavoriteController {

    @Autowired
    private FavoriteService service;
    
    @Transactional
    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestBody Favorite favorite) {
        try {
            Favorite saved = service.addFavorite(favorite);
            return ResponseEntity.ok(Map.of("status", "success", "data", saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.getFavoritesByUser(userId));
    }
    
    @Transactional
    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        try {
            service.removeFavorite(userId, productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Removed from favorites"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
