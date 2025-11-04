package com.ecommerce.paymentservice.service;


import com.ecommerce.dto.PaymentResponse;

public interface PaymentService {

	public PaymentResponse processPayment(Long orderId, Double amount);
	public PaymentResponse getPaymentByOrder(Long orderId);
	
	  void refundPayment(Long orderId, Double amount);
}
