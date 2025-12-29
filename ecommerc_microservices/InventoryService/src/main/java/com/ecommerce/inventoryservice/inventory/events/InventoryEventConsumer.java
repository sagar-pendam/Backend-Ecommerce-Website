package com.ecommerce.inventoryservice.inventory.events;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.events.InventoryEvent;
import com.ecommerce.inventoryservice.service.IInventoryMgmtService;

@Service
public class InventoryEventConsumer {
	 private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);
	@Autowired
    private IInventoryMgmtService inventoryService;
	@Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    
	@KafkaListener(topics = "inventory-events", groupId = "inventory-group")
	public void handleInventoryEvent(InventoryEvent event) {
		 log.info("Inventory Event received: {}", event);
        System.out.println("Inventory Event Started: ");
	    boolean reserved = inventoryService.reserveProduct(
	            event.getProductCode(),
	            event.getQuantity()
	    );
	    log.info("Reservation result for product {} → {}", event.getProductCode(), reserved);
	    InventoryEvent response = new InventoryEvent(
	    	    event.getOrderId(),
	    	    event.getProductCode(),
	    	    event.getQuantity(),
	    	    reserved ? "INVENTORY_CONFIRMED" : "OUT_OF_STOCK" 
	    	);
        System.out.println("Inventory Event received: " + response);
        log.info("Publishing inventory response event: {}", response);
	    kafkaTemplate.send("inventory-response-events", response);
	}

}
