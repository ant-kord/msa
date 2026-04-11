package com.example.payment;

import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.dto.request.PaymentRequest;
import com.example.payment.enums.PaymentMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateGetListDelete() {
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        PaymentRequest req = PaymentRequest.builder()
                .orderId("11111111-1111-1111-1111-111111111111")
                .amount(21.0)
                .method(PaymentMethod.CREDIT_CARD)
                .paymentDetails(PaymentDetailsDTO.builder().cardLast("4242").build())
                .build();

        var createResp = client.post().uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .returnResult();

        String body = new String(createResp.getResponseBody());
        String id = null;
        try {
            var node = mapper.readTree(body);
            id = node.get("id").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        client.get().uri("/payments/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.orderId").isEqualTo(req.getOrderId());

        client.get().uri("/payments")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isArray();

        client.delete().uri("/payments/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
