package com.ecommerce.inventoryservice.inventory.events;


import com.ecommerce.events.InventoryEvent;
import com.ecommerce.inventoryservice.service.IInventoryMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventConsumer {
	@Autowired
    private IInventoryMgmtService inventoryService;
	@Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    
	@KafkaListener(topics = "inventory-events", groupId = "inventory-group")
	public void handleInventoryEvent(InventoryEvent event) {
        System.out.println("Inventory Event Started: ");
	    boolean reserved = inventoryService.reserveProduct(
	            event.getProductCode(),
	            event.getQuantity()
	    );

	    InventoryEvent response = new InventoryEvent(
	    	    event.getOrderId(),
	    	    event.getProductCode(),
	    	    event.getQuantity(),
	    	    reserved ? "INVENTORY_CONFIRMED" : "OUT_OF_STOCK"  // ✅ plain String
	    	);
        System.out.println("Inventory Event received: " + response);
	    kafkaTemplate.send("inventory-response-events", response);
	}

}
