package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository repo;

    public List<CartItem> getUserCart(Long userId) {
        return repo.findByUserId(userId);
    }
    @Transactional
    public CartItem addToCart(CartItem item) {
        // ✅ Check if the same product already exists in the user's cart
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(item.getUserId(), item.getProductId());

        if (existingItem.isPresent()) {
            // ✅ Update quantity instead of adding new row
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
            return repo.save(cartItem);
        } else {
            // ✅ Add as a new item
            return repo.save(item);
        }
    }
    
 // Increase quantity by 1
    @Transactional
    public CartItem increaseQuantity(Long userId, Long productId) {
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            return repo.save(cartItem);
        }
        return null; // or throw exception
    }

    // Decrease quantity by 1
    @Transactional
    public CartItem decreaseQuantity(Long userId, Long productId) {
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQty = cartItem.getQuantity() - 1;

            if (newQty <= 0) {
                // Remove item if quantity becomes 0
                repo.delete(cartItem);
                return null;
            } else {
                cartItem.setQuantity(newQty);
                return repo.save(cartItem);
            }
        }
        return null;
    }


    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        repo.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
        repo.deleteAll(repo.findByUserId(userId));
    }
}
