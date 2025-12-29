package com.ecommerce.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationServiceWIthJwtApplication {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceWIthJwtApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Authentication Service...");
        SpringApplication.run(AuthenticationServiceWIthJwtApplication.class, args);
        logger.info("Authentication Service Started Successfully!");
    }
}