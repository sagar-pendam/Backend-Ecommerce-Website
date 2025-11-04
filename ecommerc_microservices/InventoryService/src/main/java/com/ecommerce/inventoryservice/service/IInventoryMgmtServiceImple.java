package com.ecommerce.inventoryservice.service;

import java.util.Optional;

import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class IInventoryMgmtServiceImple implements IInventoryMgmtService {
	@Autowired
	private InventoryRepository repository;
	@Override
	public boolean isInStock(String productCode, int quantity) {
		// TODO Auto-generated method stub
		return repository.findByProductCode(productCode).map(inventory -> inventory.getQuantity() >= quantity).orElse(false);
	}
	
	public boolean reserveProduct(String productCode, int quantity) {
        Optional<Inventory> inventoryOpt = repository.findByProductCode(productCode);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            if (inventory.getQuantity() >= quantity) {
                inventory.setQuantity(inventory.getQuantity() - quantity);
                repository.save(inventory);
                return true;
            }
        }
        return false;
    }

}
