package com.ecommerce.enums;

public enum InventoryStatus {
	 RESERVE,             // Request to reserve stock
	    RELEASE,             // Release reserved stock
	    INVENTORY_CONFIRMED, // Stock successfully reserved
	    OUT_OF_STOCK         // Stock unavailable
}
