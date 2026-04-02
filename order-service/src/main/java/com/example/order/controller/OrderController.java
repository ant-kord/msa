package com.example.order.controller;

import com.example.order.controller.doc.OrderControllerDoc;
import com.example.order.domain.Order;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderControllerDoc {

    private final OrderService orderService;

    @Override
    @PostMapping
    @CircuitBreaker(name = "orderServiceCircuitBreaker", fallbackMethod = "createOrderFallback")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        try {
            // Эмуляция случайного сбоя (500 ошибка) для теста Retry
            /*if (Math.random() < 0.7) {
                log.warn("Имитация сетевого сбоя для заказа: {}", request.getOrderId());
                return ResponseEntity.status(500).build();
            }*/

            Order created = orderService.createOrder(request);
            return new ResponseEntity<>(toResponse(created), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create order");
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return orderService.getOrder(id)
                .map(o -> ResponseEntity.ok(toResponse(o)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable String id, @RequestBody OrderRequest request,
                                                     @RequestParam(value = "status", required = false) OrderStatus status) {
        try {
            return orderService.updateOrder(id, request, status)
                    .map(o -> ResponseEntity.ok(toResponse(o)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        boolean deleted = orderService.deleteOrder(id);
        if (deleted) return ResponseEntity.noContent().build();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
    }

    public ResponseEntity<OrderResponse> createOrderFallback(OrderRequest request, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .customerId(o.getCustomerId())
                .items(o.getItems().stream().map(it -> com.example.order.dto.OrderItemRequest.builder()
                        .productId(it.getProductId())
                        .quantity(it.getQuantity())
                        .price(it.getPrice())
                        .build()).collect(Collectors.toList()))
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
