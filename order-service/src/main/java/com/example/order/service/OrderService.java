package com.example.order.service;

import com.example.order.integration.payment.client.PaymentClient;
import com.example.order.domain.Order;
import com.example.order.domain.OrderItem;
import com.example.order.dto.OrderStatus;
import com.example.order.dto.OrderItemRequest;
import com.example.order.dto.OrderRequest;
import com.example.order.integration.payment.config.properties.RabbitMqPaymentServiceProperties;
import com.example.order.integration.payment.dto.enums.PaymentMethod;
import com.example.order.integration.payment.dto.request.PaymentRequest;
import com.example.order.integration.payment.dto.request.PaymentRequestMessage;
import com.example.order.integration.payment.dto.response.PaymentResponse;
import com.example.order.integration.payment.dto.response.PaymentResponseMessage;
import com.example.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqPaymentServiceProperties props;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order orderSaved = createAndSaveOrder(request);
        sendPaymentMessage(orderSaved);
        return orderSaved;
    }

    @Deprecated
    @Transactional
    public Order createOrderOld(OrderRequest request) {

        Order orderSaved = createAndSaveOrder(request);


        PaymentRequest paymentReq = PaymentRequest.builder()
                .orderId(orderSaved.getId())
                .amount(orderSaved.getTotalAmount())
                .method(PaymentMethod.CREDIT_CARD)
                .paymentDetails(null)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();

        try {
            PaymentResponse paymentResp = paymentClient.createPayment(paymentReq, request.getOrderId());
            log.info("Payment created: {}", paymentResp);
            // при необходимости — обновить статус заказа в зависимости от ответа платежа
            orderSaved.setStatus(OrderStatus.PAID); // пример, если хотите
            orderRepository.save(orderSaved);
            log.info("Order save: {}", orderSaved);
        } catch (Exception ex) {
            // логирование и/или обработка ошибки: по бизнес-логике можно откатить создание заказа или ставить статус PAYMENT_FAILED
            throw new IllegalStateException("Payment creation failed: " + ex.getMessage(), ex);
        }

        return orderSaved;
    }

    @Transactional
    public void changePaymentStatus(PaymentResponseMessage response) {

        log.info("Changing status for orderId={} to={}", response.orderId(), response.status());
        Optional<Order> order = orderRepository.findById(response.orderId().toString());
        order.ifPresent(value -> value.setStatus(OrderStatus.PAID));
        log.info("Updated order status for id={}", response.orderId());
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

    private Order createAndSaveOrder(OrderRequest request) {
        validateRequest(request);
        Order order = Order.builder()
                //.id(request.getOrderId())
                .customerId(request.getCustomerId())
                .items(mapItems(request.getItems()))
                .status(OrderStatus.PENDING)
                .build();

        order.setTotalAmount(order.computeTotal());
        Order orderSaved = orderRepository.save(order);
        log.info("Order saved: {}", orderSaved);
        return orderSaved;
    }

    private void sendPaymentMessage(Order order) {
        PaymentRequestMessage requestMessage = PaymentRequestMessage.builder()
                .orderId(UUID.fromString(order.getId()))
                .amount(order.getTotalAmount())

                .build();

        rabbitTemplate.convertAndSend(
                props.exchangeRequestName(),
                props.queueRequestName(),
                requestMessage
        );
        log.info("Sent payment request for orderId={}", order.getId());
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
