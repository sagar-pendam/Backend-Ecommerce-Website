package com.ecommerce.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEvent {
    private Long orderId;
    private String productCode;
    private Integer quantity;
    private String status;
}
