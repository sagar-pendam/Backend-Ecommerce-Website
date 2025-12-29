package com.ecommerce.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/order-api/verify-purchase")
    Boolean hasPurchased(
        @RequestParam("userId") Long userId,
        @RequestParam("productCode") String productCode
    );
}
