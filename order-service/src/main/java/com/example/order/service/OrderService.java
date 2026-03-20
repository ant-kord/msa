package com.example.order.service;

import com.example.order.integration.payment.client.feign.PaymentClient;
import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderRequest;
import com.example.order.integration.payment.dto.PaymentMethod;
import com.example.order.integration.payment.dto.PaymentRequest;
import com.example.order.integration.payment.dto.PaymentResponse;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    @Transactional
    public Order createOrder(OrderRequest request) {
        validateRequest(request);

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .items(mapItems(request.getItems()))
                .status(OrderStatus.PENDING)
                .build();

        order.setTotalAmount(order.computeTotal());
        Order saved = orderRepository.save(order);

        log.info("Order created: {}", saved);

        PaymentRequest paymentReq = PaymentRequest.builder()
                .orderId(saved.getId())
                .amount(saved.getTotalAmount())
                .method(PaymentMethod.CREDIT_CARD)
                .paymentDetails(null)
                .build();

        try {
            PaymentResponse paymentResp = paymentClient.createPayment(paymentReq);
            log.info("Payment created: {}", paymentResp);
            // при необходимости — обновить статус заказа в зависимости от ответа платежа
            saved.setStatus(OrderStatus.PAID); // пример, если хотите
            orderRepository.save(saved);
            log.info("Order save: {}", saved);
        } catch (Exception ex) {
            // логирование и/или обработка ошибки: по бизнес-логике можно откатить создание заказа или ставить статус PAYMENT_FAILED
            throw new IllegalStateException("Payment creation failed: " + ex.getMessage(), ex);
        }

        return saved;
    }

    public Optional<Order> getOrder(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return orderRepository.findById(id);
    }

    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> updateOrder(String id, OrderRequest request, OrderStatus status) {
        if (id == null || id.isBlank()) return Optional.empty();
        return orderRepository.findById(id).map(existing -> {
            if (request.getCustomerId() != null && !request.getCustomerId().isBlank()) {
                existing.setCustomerId(request.getCustomerId());
            }
            if (!CollectionUtils.isEmpty(request.getItems())) {
                existing.setItems(mapItems(request.getItems()));
            }
            if (status != null) existing.setStatus(status);
            existing.setTotalAmount(existing.computeTotal());
            return orderRepository.save(existing);
        });
    }

    public boolean deleteOrder(String id) {
        if (id == null || id.isBlank()) return false;
        return orderRepository.findById(id).map(o -> {
            orderRepository.delete(o);
            return true;
        }).orElse(false);
    }

    private void validateRequest(OrderRequest request) {
        if (request == null) throw new IllegalArgumentException("OrderRequest cannot be null");
        if (request.getCustomerId() == null || request.getCustomerId().isBlank())
            throw new IllegalArgumentException("customerId is required");
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("items are required");
        for (OrderItemRequest it : request.getItems()) {
            if (it.getProductId() == null || it.getProductId().isBlank())
                throw new IllegalArgumentException("productId is required for each item");
            if (it.getQuantity() <= 0) throw new IllegalArgumentException("quantity must be > 0");
            if (it.getPrice() < 0) throw new IllegalArgumentException("price must be >= 0");
        }
    }

    private List<OrderItem> mapItems(List<OrderItemRequest> items) {
        return items.stream().map(i -> OrderItem.builder()
                .productId(i.getProductId())
                .quantity(i.getQuantity())
                .price(i.getPrice())
                .build()).collect(Collectors.toList());
    }
}
