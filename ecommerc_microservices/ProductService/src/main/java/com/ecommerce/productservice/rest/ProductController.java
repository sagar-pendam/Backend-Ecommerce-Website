package com.ecommerce.productservice.rest;

import java.util.List;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-api")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService service;

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        logger.info("Request received: Add new product");
        Product saved = service.saveProduct(product);
        logger.info("Product added successfully: {}", saved.getProductCode());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/add-all")
    public ResponseEntity<String> addAllProducts(@RequestBody List<Product> products) {
        logger.info("Request received: Add multiple products. Count = {}", products.size());
        String msg = service.saveAllProducts(products);
        logger.info("All products saved successfully");
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAll() {
        logger.info("Request received: Fetch all products");
        List<Product> list = service.getAllProducts();
        logger.info("Total products fetched = {}", list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/product/{code}")
    public ResponseEntity<Product> getByCode(@PathVariable("code") String code) {
        logger.info("Request received: Fetch product by code = {}", code);
        Product p = service.getByCode(code);  // If not found → ExceptionAdvice will handle
        logger.info("Product fetched successfully: {}", code);
        return ResponseEntity.ok(p);
    }
}
