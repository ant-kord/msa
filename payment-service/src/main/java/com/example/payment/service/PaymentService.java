package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.dto.request.PaymentRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    Payment createPayment(PaymentRequest request);

    Payment createPayment(UUID orderId, UUID customerId, double amount);

    Optional<Payment> getPayment(String id);

    List<Payment> listPayments();

    Optional<Payment> updatePayment(String id, PaymentRequest request);

    boolean deletePayment(String id);

    void deleteByOrderId(String orderId);

}
