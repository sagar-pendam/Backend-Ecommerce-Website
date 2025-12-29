package com.ecommerce.productservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

	private static Logger logger = LoggerFactory.getLogger(ProductServiceApplication.class);

    public static void main(String[] args) {
        logger.debug("ProductServiceApplication Start of main(-) method");
        SpringApplication.run(ProductServiceApplication.class, args);
        logger.debug("ProductServiceApplication End of main(-) method");
    }
}
