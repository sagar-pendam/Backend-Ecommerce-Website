package com.ecommerce.favoriteservice.service;

import com.ecommerce.favoriteservice.model.Favorite;
import com.ecommerce.favoriteservice.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository repo;

    public Favorite addFavorite(Favorite favorite) {
        if (repo.existsByUserIdAndProductId(favorite.getUserId(), favorite.getProductId())) {
            throw new RuntimeException("Product already in favorites");
        }
        return repo.save(favorite);
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public void removeFavorite(Long userId, Long productId) {
        if (!repo.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product not found in favorites");
        }
        repo.deleteByUserIdAndProductId(userId, productId);
    }
}
