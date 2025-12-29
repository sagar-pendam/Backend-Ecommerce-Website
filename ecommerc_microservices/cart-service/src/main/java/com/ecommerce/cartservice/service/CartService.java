package com.ecommerce.cartservice.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;

@Service
public class CartService {
	private static final Logger log = LoggerFactory.getLogger(CartService.class);
    @Autowired
    private CartRepository repo;

    public List<CartItem> getUserCart(Long userId) {
    	log.info("Fetching user cart from DB for userId={}", userId);
        return repo.findByUserId(userId);
    }
    @Transactional
    public CartItem addToCart(CartItem item) {
    	 log.info("Attempt to add product to cart: userId={}, productId={}, qty={}",
                 item.getUserId(), item.getProductId(), item.getQuantity());
        // Check if the same product already exists in the user's cart
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(item.getUserId(), item.getProductId());

        if (existingItem.isPresent()) {
            // Update quantity instead of adding new row
            CartItem cartItem = existingItem.get();
            log.info("Product already in cart. Increasing quantity. OldQty={}, AddedQty={}",
                    cartItem.getQuantity(), item.getQuantity());

            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
            log.info("Updated quantity: NewQty={}");
            return repo.save(cartItem);
        } else {
        	 log.info("Product not in cart. Adding new cart item.");
            //  Add as a new item
            return repo.save(item);
        }
    }
    
 // Increase quantity by 1
    @Transactional
    public CartItem increaseQuantity(Long userId, Long productId) {
    	log.info("Increasing quantity for userId={}, productId={}", userId, productId);
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            log.info("Product quantity increased: NewQty={}", cartItem.getQuantity());
            return repo.save(cartItem);
        }
        log.warn("Cannot increase quantity: Item not found in cart");
        return null; // or throw exception
    }

    // Decrease quantity by 1
    @Transactional
    public CartItem decreaseQuantity(Long userId, Long productId) {
    	log.info("Decreasing quantity for userId={}, productId={}", userId, productId);
        Optional<CartItem> existingItem = repo.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQty = cartItem.getQuantity() - 1;

            if (newQty <= 0) {
            	 log.warn("Quantity dropped to zero. Removing item from cart.");
                // Remove item if quantity becomes 0
                repo.delete(cartItem);
                return null;
            } else {
                cartItem.setQuantity(newQty);
                log.info("Quantity decreased: NewQty={}", newQty);
                return repo.save(cartItem);
            }
        }
        log.warn("Cannot decrease quantity: Item not found in cart");
        return null;
    }


    @Transactional
    public void removeFromCart(Long userId, Long productId) {
    	 log.warn("Deleting productId={} from userId={} cart", productId, userId);
        repo.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearCart(Long userId) {
    	log.warn("Clearing all items for userId={}", userId);
        repo.deleteAll(repo.findByUserId(userId));
    }
}
