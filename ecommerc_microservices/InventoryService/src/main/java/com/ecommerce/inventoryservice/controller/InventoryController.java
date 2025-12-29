package com.ecommerce.inventoryservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.inventoryservice.service.IInventoryMgmtService;

@RestController
@RequestMapping("/inventory-api")
public class InventoryController {
	 private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
	@Autowired
	private IInventoryMgmtService service;
    @GetMapping("/checkStock/{productCode}/{quantity}")
    public ResponseEntity<Boolean> checkStock(@PathVariable String productCode,
                                              @PathVariable int quantity) {
    	 log.info("API call: checkStock for productCode={} quantity={}", productCode, quantity);
        Boolean checkQuantity = service.isInStock(productCode, quantity);
        log.info("Stock check result for productCode={} → {}", productCode, checkQuantity);
        return new ResponseEntity<>(checkQuantity, HttpStatus.ACCEPTED); 
    }
}
