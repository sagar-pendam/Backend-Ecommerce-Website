package com.ecommerce.configserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SpringBootMsConfigServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootMsConfigServerApplication.class);

    public static void main(String[] args) {

        logger.info("Starting Config Server...");

        try {
            SpringApplication.run(SpringBootMsConfigServerApplication.class, args);
            logger.info("Config Server started successfully.");
        } catch (Exception e) {
            logger.error("Config Server failed to start due to error: ", e);
        }

        logger.debug("Config Server main() method execution completed.");
    }
}
