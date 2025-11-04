package com.ecommerce.productservice.advice;

import com.ecommerce.productservice.exception.ProductNotFound;
import com.ecommerce.productservice.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ProductControllerAdvice {

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFound exception) {
        return  new ResponseEntity<String>(exception.getMessage(),HttpStatus.NOT_FOUND);
    }
}
