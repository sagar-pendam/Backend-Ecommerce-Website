package com.ecommerce.paymentservice.service;


import com.ecommerce.dto.PaymentResponse;

public interface PaymentService {

	public PaymentResponse processPayment(Long orderId, Double amount,String paymentMethod);
	public PaymentResponse getPaymentByOrder(Long orderId);
	public void updatePaymentStatus(Long orderId, String status);
	public  void refundPayment(Long orderId, Double amount);
	  public void confirmStripePayment(Long orderId, String paymentIntentId);
}
