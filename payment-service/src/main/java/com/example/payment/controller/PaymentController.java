package com.example.payment.controller;

import com.example.payment.domain.Payment;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService svc;

    public PaymentController(PaymentService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest req) {
        try {
            Payment p = svc.createPayment(req);
            return new ResponseEntity<>(toResponse(p), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create payment");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> get(@PathVariable String id) {
        return svc.getPayment(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> list() {
        return ResponseEntity.ok(svc.listPayments().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable String id, @RequestBody PaymentRequest req) {
        try {
            return svc.updatePayment(id, req)
                    .map(p -> ResponseEntity.ok(toResponse(p)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean deleted = svc.deletePayment(id);
        if (deleted) return ResponseEntity.noContent().build();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentDetailsDTO details = null;
        if (p.getPaymentDetails() != null) {
            details = PaymentDetailsDTO.builder()
                    .cardLast4(p.getPaymentDetails().getCardLast4())
                    .provider(p.getPaymentDetails().getProvider())
                    .build();
        }
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .method(p.getMethod())
                .paymentDetails(details)
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
