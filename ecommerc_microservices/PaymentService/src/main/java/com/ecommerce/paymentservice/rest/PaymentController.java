package com.ecommerce.paymentservice.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/payment-api")
public class PaymentController {

    @Autowired
    private PaymentService service;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);


    @PostMapping("/pay/{orderId}")
    public ResponseEntity<PaymentResponse> makePayment(
            @PathVariable("orderId") Long orderId,
            @RequestParam("amount") Double amount,
            @RequestParam("paymentMethod") String paymentMethod
    ) {
        log.info("Payment request received → orderId={}, amount={}, method={}",
                 orderId, amount, paymentMethod);

        PaymentResponse payment = service.processPayment(orderId, amount, paymentMethod);

        log.info("Payment response → {}", payment);
        return ResponseEntity.ok(payment);
    }



    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable("orderId") Long orderId) {
        log.info("🔍 Fetching payment for OrderID: {}", orderId);

        PaymentResponse payment = service.getPaymentByOrder(orderId);

        log.info("📤 Payment fetched: {}", payment);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(
            @RequestParam("orderId") Long orderId,
            @RequestParam("paymentIntentId") String paymentIntentId) {

        log.info("🔐 Confirming Stripe payment → orderId={}, paymentIntentId={}",
                orderId, paymentIntentId);
        try {
            service.confirmStripePayment(orderId, paymentIntentId);
            log.info("✅ Stripe payment confirmed for order {}", orderId);
            return ResponseEntity.ok("Payment confirmed successfully!");
        } catch (Exception e) {
            log.error("❌ Payment confirmation failed for order {}", orderId, e);
            return ResponseEntity.status(500).body("Payment confirmation failed!");
        }
    }

    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam("status") String status) {

        log.info("🔄 Updating payment status → OrderID={}, Status={}", orderId, status);

        service.updatePaymentStatus(orderId, status);

        log.info("✅ Payment status updated for OrderID={}", orderId);
        return ResponseEntity.ok("Payment status updated to: " + status);
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<String> refundPayment(
            @PathVariable("orderId") Long orderId,
            @RequestParam("amount") Double amount) {

        log.info("🔁 Refund request → OrderID={}, Amount={}", orderId, amount);

        service.refundPayment(orderId, amount);

        log.info("✅ Refund processed successfully → OrderID={}", orderId);
        return ResponseEntity.ok("Refund processed successfully for Order: " + orderId);
    }
}