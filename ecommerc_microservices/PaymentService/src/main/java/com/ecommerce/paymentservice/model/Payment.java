package com.ecommerce.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Double amount;

    private String status;  // SUCCESS, FAILED, REFUNDED

    @Column(unique = true, nullable = false)
    private String transactionId;  // ✅ helpful for refunds & audits

    private LocalDateTime createdAt = LocalDateTime.now(); // ✅ timestamp for tracing
}
