package com.ecommerce.cartservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Reference to the user who owns the cart
    @Column(nullable = false)
    private Long userId;

    // ✅ Reference to product in Product Service
    @Column(nullable = false)
    private Long productId;

    // ✅ Product snapshot data (to avoid multiple calls to Product Service)
    private String productName;
    private String productCode;
    private String imageUrl;

    private double price;
    private int quantity;

    public double getTotalPrice() {
        return price * quantity;
    }
}
