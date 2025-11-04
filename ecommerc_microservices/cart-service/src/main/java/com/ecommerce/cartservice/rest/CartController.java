package com.ecommerce.cartservice.rest;

import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-api")
public class CartController {

    @Autowired
    private CartService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.getUserCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem item) {
        return ResponseEntity.ok(service.addToCart(item));
    }

    @DeleteMapping("/remove/{userId}/{productId}")
    public ResponseEntity<String> removeItem(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        service.removeFromCart(userId, productId);
        return ResponseEntity.ok("Item removed from cart");
    }
    
    @PutMapping("/increase/{userId}/{productId}")
    public ResponseEntity<CartItem> increaseQuantity(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        return new ResponseEntity<CartItem>(service.increaseQuantity(userId, productId),HttpStatus.ACCEPTED);
    }

    @PutMapping("/decrease/{userId}/{productId}")
    public ResponseEntity<CartItem> decreaseQuantity(@PathVariable("userId") Long userId, @PathVariable("productId") Long productId) {
        return new ResponseEntity<CartItem>(service.decreaseQuantity(userId, productId),HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/clear/{userId}")
    
    public ResponseEntity<String> clearCart(@PathVariable("userId") Long userId) {
        service.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
