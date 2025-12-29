package com.ecommerce.favoriteservice.service;

import com.ecommerce.favoriteservice.model.Favorite;
import com.ecommerce.favoriteservice.repository.FavoriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteService.class);

    @Autowired
    private FavoriteRepository repo;

    public Favorite addFavorite(Favorite favorite) {
        logger.debug("Attempting to add favorite: userId={}, productId={}", 
                        favorite.getUserId(), favorite.getProductId());

        if (repo.existsByUserIdAndProductId(favorite.getUserId(), favorite.getProductId())) {
            logger.warn("Product already in favorites → userId={}, productId={}", 
                         favorite.getUserId(), favorite.getProductId());
            throw new RuntimeException("Product already in favorites");
        }

        Favorite saved = repo.save(favorite);
        logger.info("Favorite added successfully: {}", saved);
        return saved;
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        logger.info("Fetching favorites for userId={}", userId);
        return repo.findByUserId(userId);
    }

    public void removeFavorite(Long userId, Long productId) {
        logger.debug("Attempting to remove favorite → userId={}, productId={}", userId, productId);

        if (!repo.existsByUserIdAndProductId(userId, productId)) {
            logger.error("Favorite not found → userId={}, productId={}", userId, productId);
            throw new RuntimeException("Product not found in favorites");
        }

        repo.deleteByUserIdAndProductId(userId, productId);
        logger.info("Favorite removed → userId={}, productId={}", userId, productId);
    }
}
