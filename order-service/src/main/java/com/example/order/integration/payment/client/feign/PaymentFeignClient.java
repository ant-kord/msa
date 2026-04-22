package com.example.order.integration.payment.client.feign;


import com.example.order.integration.payment.dto.request.PaymentRequest;
import com.example.order.integration.payment.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

//@FeignClient(name = "payment-service", url = "${integration.payment-service.base-url}")
public interface PaymentFeignClient {

    /*@PostMapping
    PaymentResponse createPayment(@RequestBody PaymentRequest request,
                                  @RequestHeader(value = "X-Idempotency-Key", required = false)
                                  String idempotencyKey);*/
}
