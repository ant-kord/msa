package com.example.order.integration.payment.client;

import com.example.order.integration.payment.dto.PaymentMethod;
import com.example.order.integration.payment.dto.PaymentRequest;
import com.example.order.integration.payment.dto.PaymentResponse;
import com.example.order.integration.payment.dto.PaymentStatus;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock( // Включает и настраивает WireMock сервер для мокирования внешних HTTP-запросов
        @ConfigureWireMock(
                name = "payment-service", // Название мок-сервиса
                port = 9999, // Порт, на котором работает WireMock
                baseUrlProperties = "http://localhost", // Базовый URL для клиента
                filesUnderClasspath = "wiremock" // Папка с файлами сценариев мок-ответов
        )
)
public class PaymentClientIntegrationTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    void testPayment_Success() {

        PaymentRequest request = buildPaymentRequest("200");
        PaymentResponse response = paymentClient.createPayment(request, request.getOrderId());

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(postRequestedFor(urlEqualTo("/api/v1/payments"))
                .withHeader("X-Idempotency-Key", equalTo(request.getOrderId())));
    }

    @Test
    void testPayment_Conflict() {

        PaymentRequest request = buildPaymentRequest("409");

        assertThrows(FeignException.Conflict.class, () -> {
            paymentClient.createPayment(request, request.getOrderId());;
        });
    }

    @Test
    void testPayment_NotFound() {

        PaymentRequest request = buildPaymentRequest("404");

        assertThrows(FeignException.NotFound.class, () -> {
            paymentClient.createPayment(request, request.getOrderId());;
        });
    }

    @Test
    void testPayment_ServerError() {

        PaymentRequest request = buildPaymentRequest("500");

        assertThrows(FeignException.InternalServerError.class, () -> {
            paymentClient.createPayment(request, request.getOrderId());
        });
    }

    @Test
    void testPayment_BadRequest() {

        PaymentRequest request = buildPaymentRequest("400");

        assertThrows(FeignException.BadRequest.class, () -> {
            paymentClient.createPayment(request, request.getOrderId());
        });
    }

    private PaymentRequest buildPaymentRequest(String orderId) {
        return PaymentRequest
                .builder()
                .orderId(orderId)
                .amount(10.0)
                .method(PaymentMethod.CREDIT_CARD)
                .paymentDetails(null)
                .build();
    }
}
