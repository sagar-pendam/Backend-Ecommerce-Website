package com.ecommerce.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data                   // generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // generates default constructor
@AllArgsConstructor     // generates constructor with all fields
public class RefundEvent implements Serializable {
    private Long orderId;
    private Double amount;
}
