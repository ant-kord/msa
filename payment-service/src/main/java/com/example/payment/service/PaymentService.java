package com.example.payment.service;

import com.example.payment.domain.Payment;
import com.example.payment.domain.PaymentDetails;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.integration.order.dto.request.PaymentRequestMessage;
import com.example.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    @Transactional
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
        return repo.save(p);
    }

    @Transactional
    public Payment createPayment(PaymentRequestMessage request) {
        log.info("Create payment request: {}", request);
        validate(request);
        Payment p = Payment.builder()
                .orderId(String.valueOf(request.orderId()))
                .amount(request.amount())
                .method(request.method())
                .paymentDetails(mapDetails(request.paymentDetails()))
                .build();
        log.info("Payment created: {}", p);
        return repo.save(p);
    }

    public Optional<Payment> getPayment(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return repo.findById(id);
    }

    public List<Payment> listPayments() {
        return repo.findAll();
    }

    public Optional<Payment> updatePayment(String id, PaymentRequest request) {
        if (id == null || id.isBlank()) return Optional.empty();
        return repo.findById(id).map(existing -> {
            if (request.getOrderId() != null && !request.getOrderId().isBlank())
                existing.setOrderId(request.getOrderId());
            if (request.getAmount() != null && request.getAmount() > 0)
                existing.setAmount(request.getAmount());
            if (request.getMethod() != null) existing.setMethod(request.getMethod());
            if (request.getPaymentDetails() != null)
                existing.setPaymentDetails(mapDetails(request.getPaymentDetails()));
            if (request.getStatus() != null) existing.setStatus(request.getStatus());
            return repo.save(existing);
        });
    }

    public boolean deletePayment(String id) {
        if (id == null || id.isBlank()) return false;
        return repo.findById(id).map(p -> {
            repo.delete(p);
            return true;
        }).orElse(false);
    }

    private void validate(PaymentRequest request) {
        if (request == null) throw new IllegalArgumentException("PaymentRequest cannot be null");
        if (request.getOrderId() == null || request.getOrderId().isBlank())
            throw new IllegalArgumentException("orderId is required");
        if (request.getAmount() == null || request.getAmount() <= 0)
            throw new IllegalArgumentException("amount must be greater than 0");
        //if (request.getMethod() == null) throw new IllegalArgumentException("method is required");
    }

    private void validate(PaymentRequestMessage request) {
        if (request == null) throw new IllegalArgumentException("PaymentRequest cannot be null");
        if (request.orderId() == null || request.orderId().toString().isBlank())
            throw new IllegalArgumentException("orderId is required");
        if (request.amount() == null || request.amount() <= 0)
            throw new IllegalArgumentException("amount must be greater than 0");
        //if (request.getMethod() == null) throw new IllegalArgumentException("method is required");
    }

    private PaymentDetails mapDetails(PaymentDetailsDTO dto) {
        if (dto == null) return null;
        return PaymentDetails.builder().cardLast(dto.getCardLast()).provider(dto.getProvider()).build();
    }
}
