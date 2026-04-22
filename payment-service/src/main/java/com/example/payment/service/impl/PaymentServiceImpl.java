package com.example.payment.service.impl;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentDetails;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.dto.request.PaymentRequest;
import com.example.payment.enums.PaymentStatus;
import com.example.payment.integration.order.dto.message.OrderCreationStatus;
import com.example.payment.integration.order.dto.message.OrderCreationStatusMessage;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, OrderCreationStatusMessage> kafkaTemplate;

    @Value("${kafka.service.order.order-creation-status-topic}")
    private String orderCreationStatusTopic;

    @Transactional
    @Override
    public Payment createPayment(PaymentRequest request) {
        log.info("Create payment request: {}", request);
        validate(request);
        Payment p = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .method(request.getMethod())
                .paymentDetails(mapDetails(request.getPaymentDetails()))
                .build();
        log.info("Payment created: {}", p);
        return paymentRepository.save(p);
    }

    @Transactional
    @Override
    public Payment createPayment(UUID orderId, UUID customerId, double amount) {
        log.info("Create payment orderId: {}", orderId);
        var payment = Payment.builder()
                .orderId(String.valueOf(orderId))
                .customerId(String.valueOf(customerId))
                .amount(amount)
                .build();

        boolean paymentCompleted = tryProcessPayment(orderId);

        if (paymentCompleted) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        paymentRepository.save(payment);
        log.info("Payment created: {}", payment);

        sendStatusMessage(orderId, payment.getStatus());


        return payment;
    }

    @Override
    public Optional<Payment> getPayment(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> updatePayment(String id, PaymentRequest request) {
        if (id == null || id.isBlank()) return Optional.empty();
        return paymentRepository.findById(id).map(existing -> {
            if (request.getOrderId() != null && !request.getOrderId().isBlank())
                existing.setOrderId(request.getOrderId());
            if (request.getAmount() != null && request.getAmount() > 0)
                existing.setAmount(request.getAmount());
            if (request.getMethod() != null) existing.setMethod(request.getMethod());
            if (request.getPaymentDetails() != null)
                existing.setPaymentDetails(mapDetails(request.getPaymentDetails()));
            if (request.getStatus() != null) existing.setStatus(request.getStatus());
            return paymentRepository.save(existing);
        });
    }

    @Override
    public boolean deletePayment(String id) {
        if (id == null || id.isBlank()) return false;
        return paymentRepository.findById(id).map(p -> {
            paymentRepository.delete(p);
            return true;
        }).orElse(false);
    }

    @Override
    public void deleteByOrderId(String orderId) {
        paymentRepository.deleteByOrderId(orderId);
    }

    private boolean tryProcessPayment(UUID orderId) {
        return Math.random() > 0.2;
    }

    private void sendStatusMessage(UUID orderId, PaymentStatus status) {
        var statusMessage = OrderCreationStatusMessage.builder()
                .orderId(orderId)
                .status(status.equals(PaymentStatus.COMPLETED) ? OrderCreationStatus.PAID : OrderCreationStatus.PAID_ERROR)
                .build();

        kafkaTemplate.send(orderCreationStatusTopic, statusMessage);
        log.info("Sent payment creation message to Kafka for orderId ID: {}", orderId);
    }

    private void validate(PaymentRequest request) {
        if (request == null) throw new IllegalArgumentException("PaymentRequest cannot be null");
        if (request.getOrderId() == null || request.getOrderId().isBlank())
            throw new IllegalArgumentException("orderId is required");
        if (request.getAmount() == null || request.getAmount() <= 0)
            throw new IllegalArgumentException("amount must be greater than 0");
        //if (request.getMethod() == null) throw new IllegalArgumentException("method is required");
    }


    private PaymentDetails mapDetails(PaymentDetailsDTO dto) {
        if (dto == null) return null;
        return PaymentDetails.builder().cardLast(dto.getCardLast()).provider(dto.getProvider()).build();
    }
}
