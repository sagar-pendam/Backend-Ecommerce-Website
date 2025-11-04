package com.ecommerce.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String productCode;
    private Integer quantity;
    private Double amount;
//    private OrderStatus status;  // enum
    private String status;
}
