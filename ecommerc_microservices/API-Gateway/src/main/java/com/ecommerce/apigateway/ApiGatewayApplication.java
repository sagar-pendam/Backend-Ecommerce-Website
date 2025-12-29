package com.ecommerce.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {

        logger.info("Starting API Gateway Service...");

        try {
            SpringApplication.run(ApiGatewayApplication.class, args);
            logger.info("API Gateway Service started successfully and registered with Eureka.");
        } 
        catch (Exception e) {
            logger.error("API Gateway failed to start due to an exception.", e);
        }
    }
}