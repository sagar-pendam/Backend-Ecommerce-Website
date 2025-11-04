package com.ecommerce.paymentservice.rest;


import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-api")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<PaymentResponse> makePayment(
            @PathVariable Long orderId,
            @RequestParam Double amount) {

        PaymentResponse payment = service.processPayment(orderId, amount);

        // Map entity → DTO
        PaymentResponse response = new PaymentResponse(
        	    payment.getOrderId(),
        	    payment.getAmount(),
        	    payment.getStatus()   // PaymentStatus
        	);
//        if (response.getStatus().equals("SUCCESS")) {
//            // publish PaymentSuccessEvent
//        } else {
//            // publish PaymentFailedEvent
//        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long orderId) {
        PaymentResponse payment = service.getPaymentByOrder(orderId);

        PaymentResponse response = new PaymentResponse(
            payment.getOrderId(),
            payment.getAmount(),
            payment.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}
