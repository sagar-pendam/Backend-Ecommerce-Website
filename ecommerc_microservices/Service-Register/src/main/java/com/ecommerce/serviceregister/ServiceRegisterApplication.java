package com.ecommerce.serviceregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegisterApplication {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegisterApplication.class);

    public static void main(String[] args) {

        logger.info("Starting Eureka Service Registry...");

        try {
            SpringApplication.run(ServiceRegisterApplication.class, args);
            logger.info("Eureka Service Registry started successfully.");
        } catch (Exception e) {
            logger.error("Eureka Server failed to start due to error: ", e);
        }

        logger.debug("Eureka Service Registry main() finished executing.");
    }
}
