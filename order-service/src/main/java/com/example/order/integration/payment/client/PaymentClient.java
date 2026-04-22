package com.example.order.integration.payment.client;

import com.example.order.integration.payment.client.feign.PaymentFeignClient;
import com.example.order.integration.payment.dto.request.PaymentRequest;
import com.example.order.integration.payment.dto.response.PaymentResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

/*@Slf4j
@RequiredArgsConstructor
@Component*/
public class PaymentClient {

   /* private final PaymentFeignClient paymentFeignClient;
    private final JsonMapper jsonMapper;

    @Retry(name = "paymentServiceRetry")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker")
    @RateLimiter(name = "paymentClientRateLimiter")
    @Bulkhead(name = "paymentClientBulkhead")
    public PaymentResponse createPayment(PaymentRequest request, String idempotencyKey) {
        try {
            log.info("Create payment request: {}", request);
            log.info("Idempotency key: {}", idempotencyKey);
            return paymentFeignClient.createPayment(request, idempotencyKey);
        } catch (FeignException ex) {
           return processException(ex);
        }
    }

    private PaymentResponse processException(FeignException ex) {
        HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(ex.status());
        Optional<ByteBuffer> byteBuffer = ex.responseBody();

        log.info("Response httpStatusCode: {}", httpStatusCode);
        log.info("Response body: {}", byteBuffer);
        log.info("Response body: {}",  ex.responseBody());
        if (isAcceptable(httpStatusCode) && byteBuffer.isPresent()) {
            return getResponse(byteBuffer.get());
        } else {
            throw ex;
        }
    }

    private PaymentResponse getResponse(ByteBuffer byteBuffer) {
        try {
            return jsonMapper.readValue(byteBuffer.array(), PaymentResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAcceptable(HttpStatusCode httpStatusCode) {
        return httpStatusCode.is2xxSuccessful() || httpStatusCode.is3xxRedirection();
    }*/
}
