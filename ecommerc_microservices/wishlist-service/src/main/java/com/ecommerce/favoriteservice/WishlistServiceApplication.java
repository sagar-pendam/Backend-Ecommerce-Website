package com.ecommerce.favoriteservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WishlistServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(WishlistServiceApplication.class);

    public static void main(String[] args) {

        logger.info("Wishlist Service Application starting...");
        SpringApplication.run(WishlistServiceApplication.class, args);
        logger.info("Wishlist Service Application started successfully.");
    }
}
