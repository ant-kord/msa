package com.example.delivery;

import com.example.delivery.dto.AddressDTO;
import com.example.delivery.dto.DeliveryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeliveryControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateGetListDelete() {
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        DeliveryRequest req = DeliveryRequest.builder()
                .orderId("11111111-1111-1111-1111-111111111111")
                .address(AddressDTO.builder().street("123 Main").city("City").postalCode("12345").country("Country").build())
                .build();

        var createResp = client.post().uri("/deliveries")
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

        client.get().uri("/deliveries/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.orderId").isEqualTo(req.getOrderId());

        client.get().uri("/deliveries")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isArray();

        client.delete().uri("/deliveries/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
