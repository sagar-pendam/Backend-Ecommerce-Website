package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.service.IInventoryMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory-api")
public class InventoryController {
	@Autowired
	private IInventoryMgmtService service;
    @GetMapping("/checkStock/{productCode}/{quantity}")
    public ResponseEntity<Boolean> checkStock(@PathVariable String productCode,
                                              @PathVariable int quantity) {
        Boolean checkQuantity = service.isInStock(productCode, quantity);
        return new ResponseEntity<>(checkQuantity, HttpStatus.ACCEPTED); // ✅ Correct enum
    }
}
