package com.ecommerce.cartservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CartServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(CartServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
        log.info("Cart Service is running...");
    }
}