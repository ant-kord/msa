package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.integration.order.dto.request.PaymentRequestMessage;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment createPayment(PaymentRequest request);

    Payment createPayment(PaymentRequestMessage request);

    Optional<Payment> getPayment(String id);

    List<Payment> listPayments();

    Optional<Payment> updatePayment(String id, PaymentRequest request);

    boolean deletePayment(String id);

}
