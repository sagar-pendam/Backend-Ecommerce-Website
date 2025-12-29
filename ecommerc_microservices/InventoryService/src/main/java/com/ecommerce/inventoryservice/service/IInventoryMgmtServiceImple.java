package com.ecommerce.inventoryservice.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;



@Service
public class IInventoryMgmtServiceImple implements IInventoryMgmtService {
	private static final Logger log = LoggerFactory.getLogger(IInventoryMgmtServiceImple.class);
	@Autowired
	private InventoryRepository repository;
	@Override
	public boolean isInStock(String productCode, int quantity) {
		log.info("Checking stock for productCode={} with required quantity={}", productCode, quantity);
		// TODO Auto-generated method stub
		return repository.findByProductCode(productCode).map(inventory -> inventory.getQuantity() >= quantity).orElse(false);
	}
	
	@Transactional
	public boolean reserveProduct(String productCode, int quantity) {
		 log.info("Reserving product {} with quantity={}", productCode, quantity);
        Optional<Inventory> inventoryOpt = repository.findByProductCode(productCode);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            if (inventory.getQuantity() >= quantity) {
                inventory.setQuantity(inventory.getQuantity() - quantity);
                repository.save(inventory);
                log.info("Product {} reserved successfully. Remaining quantity={}", productCode, inventory.getQuantity());
                return true;
            }
        }
        log.warn("Product {} not found in inventory", productCode);
        return false;
    }

}
