package com.example.order;

import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testCreateGetListDelete() {
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        OrderRequest req = OrderRequest.builder()
                .customerId("11111111-1111-1111-1111-111111111111")
                .items(List.of(OrderItemRequest.builder().productId("2222").quantity(2).price(10.0).build()))
                .build();

        // Create
        var createResp = client.post().uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .returnResult();

        String body = new String(createResp.getResponseBody());
        // extract id
        String id = null;
        try {
            var node = mapper.readTree(body);
            id = node.get("id").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("body " + body);
        System.out.println("id " + id);

        // Get
        client.get().uri("/orders/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.customerId").isEqualTo(req.getCustomerId());

        // List
        client.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$").isArray();

        // Delete
        client.delete().uri("/orders/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }
}
