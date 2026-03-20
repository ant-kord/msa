package com.example.payment.controller;

import com.example.payment.controller.doc.PaymentControllerDoc;
import com.example.payment.domain.Payment;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentControllerDoc {

    private final PaymentService svc;

    @Override
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest req) {
        try {
            log.info("Create payment request: {}", req);
            Payment p = svc.createPayment(req);
            log.info("Created Payment {}", p);
            return new ResponseEntity<>(toResponse(p), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create payment");
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String id) {
        return svc.getPayment(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> listPayments() {
        return ResponseEntity.ok(svc.listPayments().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable String id, @RequestBody PaymentRequest req) {
        try {
            return svc.updatePayment(id, req)
                    .map(p -> ResponseEntity.ok(toResponse(p)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        boolean deleted = svc.deletePayment(id);
        if (deleted) return ResponseEntity.noContent().build();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
    }

    private PaymentResponse toResponse(Payment p) {
        PaymentDetailsDTO details = null;
        if (p.getPaymentDetails() != null) {
            details = PaymentDetailsDTO.builder()
                    .cardLast(p.getPaymentDetails().getCardLast())
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
