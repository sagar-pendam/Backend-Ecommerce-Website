package com.ecommerce.productservice.rest;

import java.util.List;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product-api")
public class ProductController {
    @Autowired
    private ProductService service;

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return new ResponseEntity<>(service.saveProduct(product), HttpStatus.CREATED);
    }
    @PostMapping("/add-all")
    public ResponseEntity<String> addAllProducts(@RequestBody List<Product> product) {
        return new ResponseEntity<>(service.saveAllProducts(product), HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/product/{code}")
    public ResponseEntity<Product> getByCode(@PathVariable("code") String code) {
        return new ResponseEntity<>(service.getByCode(code),HttpStatus.OK);
    }
}
