package com.ecommerce.paymentservice;

import com.ecommerce.events.OrderEvent;
import com.ecommerce.events.InventoryEvent;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

@SpringBootTest
public class PaymentFlowTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testPaymentSuccessFlow() throws InterruptedException {
        System.out.println("\n🟢 TEST 1: Payment < 1000 and Inventory Available");

        OrderEvent orderEvent = new OrderEvent(
                1L,
                "P12345",
                2,
                750.0,
                "PENDING"
        );

        kafkaTemplate.send("order-events", orderEvent);
        Thread.sleep(3000); // wait for processing

        Optional<Payment> payment = paymentRepository.findByOrderId(1L);
        assert payment.isPresent();
        assert payment.get().getStatus().equalsIgnoreCase("SUCCESS");

        System.out.println("✅ Payment success flow verified!");
    }

    @Test
    public void testPaymentFailureFlow() throws InterruptedException {
        System.out.println("\n🔴 TEST 2: Payment > 1000 → Payment Fail");

        OrderEvent orderEvent = new OrderEvent(
                2L,
                "P54321",
                1,
                1500.0,
                "PENDING"
        );

        kafkaTemplate.send("order-events", orderEvent);
        Thread.sleep(3000);

        Optional<Payment> payment = paymentRepository.findByOrderId(2L);
        assert payment.isPresent();
        assert payment.get().getStatus().equalsIgnoreCase("FAILED");

        System.out.println("✅ Payment failure flow verified!");
    }

    @Test
    public void testRefundFlow() throws InterruptedException {
        System.out.println("\n💸 TEST 3: Inventory Out of Stock → Refund Triggered");

        // First, create a successful payment
        OrderEvent orderEvent = new OrderEvent(
                3L,
                "P98765",
                1,
                800.0,
                "PENDING"
        );
        kafkaTemplate.send("order-events", orderEvent);
        Thread.sleep(2000);

        // Then simulate inventory failure → refund event
        InventoryEvent inventoryEvent = new InventoryEvent(
                3L,            // orderId
                "P12345",      // productCode
                1,             // quantity
                "OUT_OF_STOCK" // status
        );

        kafkaTemplate.send("inventory-response-events", inventoryEvent);
        Thread.sleep(3000);

        Optional<Payment> payment = paymentRepository.findByOrderId(3L);
        assert payment.isPresent();
        assert payment.get().getStatus().equalsIgnoreCase("REFUNDED");

        System.out.println("✅ Refund flow verified!");
    }

    @Test
    public void testNoRefundWhenInventoryConfirmed() throws InterruptedException {
        System.out.println("\n🟢 TEST 4: Inventory Confirmed → No Refund Should Happen");

        // Step 1️⃣: Simulate a new order event
        OrderEvent orderEvent = new OrderEvent(4L,"P12345",3, 900.0,"ORDER_CREATED");
        kafkaTemplate.send("order-events", orderEvent);
        Thread.sleep(2000); // wait for payment process

        // Step 2️⃣: Simulate inventory confirmation
        InventoryEvent inventoryEvent = new InventoryEvent(4L, "P1001", 1, "INVENTORY_CONFIRMED");
        kafkaTemplate.send("inventory-response-events", inventoryEvent);
        Thread.sleep(3000); // wait for event propagation

        // Step 3️⃣: Fetch payment from DB
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(4L);
        assert paymentOpt.isPresent();

        Payment payment = paymentOpt.get();

        // Step 4️⃣: Verify that refund was NOT triggered
        assert !payment.getStatus().equalsIgnoreCase("REFUNDED") : "Refund should not have been triggered!";
        assert payment.getStatus().equalsIgnoreCase("SUCCESS");

        System.out.println("✅ No refund triggered, payment remained SUCCESS!");
    }

}
