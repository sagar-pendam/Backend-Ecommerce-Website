package com.ecommerce.productservice.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.productservice.exception.ProductNotFound;

@RestControllerAdvice
public class ProductControllerAdvice {
	 private static final Logger logger = LoggerFactory.getLogger(ProductControllerAdvice.class);
    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFound exception) {
    	logger.error("Product not found exception: {}", exception.getMessage());
        return  new ResponseEntity<String>(exception.getMessage(),HttpStatus.NOT_FOUND);
    }
}
