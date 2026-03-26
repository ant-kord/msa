package com.example.order;


import com.example.order.controller.OrderController;
import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderStatus;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_returnsCreated() throws Exception {
        OrderRequest req = OrderRequest.builder()
                .customerId("cust-1")
                .items(List.of(OrderItemRequest.builder().productId("p1").quantity(1).price(10.0).build()))
                .build();

        Order saved = Order.builder()
                .id(UUID.randomUUID().toString())
                .customerId("cust-1")
                .items(List.of(OrderItem.builder().productId("p1").quantity(1).price(10.0).build()))
                .totalAmount(10.0)
                .status(OrderStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        when(orderService.createOrder(ArgumentMatchers.any(OrderRequest.class))).thenReturn(saved);

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.customerId").value("cust-1"))
                .andExpect(jsonPath("$.totalAmount").value(10.0));
    }

    @Test
    void getOrder_returnsOk() throws Exception {
        String id = UUID.randomUUID().toString();
        Order o = Order.builder()
                .id(id)
                .customerId("cust-2")
                .items(List.of(OrderItem.builder().productId("p2").quantity(2).price(5.0).build()))
                .totalAmount(10.0)
                .status(OrderStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        when(orderService.getOrder(id)).thenReturn(Optional.of(o));

        mvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerId").value("cust-2"));
    }

    @Test
    void listOrders_returnsOk() throws Exception {
        Order o1 = Order.builder().id(UUID.randomUUID().toString()).customerId("c1").totalAmount(5.0).status(OrderStatus.PENDING).createdAt(OffsetDateTime.now()).build();
        Order o2 = Order.builder().id(UUID.randomUUID().toString()).customerId("c2").totalAmount(7.0).status(OrderStatus.PENDING).createdAt(OffsetDateTime.now()).build();

        when(orderService.listOrders()).thenReturn(List.of(o1, o2));

        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateOrder_returnsOk() throws Exception {
        String id = UUID.randomUUID().toString();
        OrderRequest req = OrderRequest.builder().customerId("updated").items(List.of()).build();

        Order updated = Order.builder()
                .id(id)
                .customerId("updated")
                .items(List.of())
                .totalAmount(0.0)
                .status(OrderStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        when(orderService.updateOrder(ArgumentMatchers.eq(id), ArgumentMatchers.any(OrderRequest.class), ArgumentMatchers.isNull())).thenReturn(Optional.of(updated));

        mvc.perform(put("/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("updated"));
    }

    @Test
    void deleteOrder_returnsNoContent() throws Exception {
        String id = UUID.randomUUID().toString();
        when(orderService.deleteOrder(id)).thenReturn(true);

        mvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isNoContent());
    }
}
