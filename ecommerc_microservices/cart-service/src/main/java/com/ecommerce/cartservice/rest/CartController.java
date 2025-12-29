package com.ecommerce.cartservice.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.service.CartService;

@RestController
@RequestMapping("/cart-api")
public class CartController {
	 private static final Logger log = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private CartService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable("userId") Long userId) {
    	  log.info("Fetching cart for userId={}", userId);
    	return ResponseEntity.ok(service.getUserCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem item) {
    	log.info("Adding item to cart: userId={}, productId={}, quantity={}",
                item.getUserId(), item.getProductId(), item.getQuantity());
        return ResponseEntity.ok(service.addToCart(item));
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<String> removeItem(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
    	 log.warn("Removing item: userId={}, productId={}", userId, productId);
    	service.removeFromCart(userId, productId);
        return ResponseEntity.ok("Item removed from cart");
    }
    
    @PutMapping("/increase/{userId}/{productId}")
    public ResponseEntity<CartItem> increaseQuantity(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
    	 log.info("Increasing quantity: userId={}, productId={}", userId, productId);
    	return new ResponseEntity<CartItem>(service.increaseQuantity(userId, productId),HttpStatus.ACCEPTED);
    }

    @PutMapping("/decrease/{userId}/{productId}")
    public ResponseEntity<CartItem> decreaseQuantity(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
    	log.info("Decreasing quantity: userId={}, productId={}", userId, productId);
    	return new ResponseEntity<CartItem>(service.decreaseQuantity(userId, productId),HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable("userId") Long userId) {
        log.warn("Clearing cart for userId={}", userId);
        service.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
