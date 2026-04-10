package com.example.payment.integration.order.dto.response;

import com.example.payment.enums.PaymentMethod;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.enums.PaymentStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentResponseMessage(
        UUID orderId,
        Double amount,
        PaymentMethod method,
        PaymentDetailsDTO paymentDetails,
        PaymentStatus status)
        implements Serializable {
}
