package com.example.order.integration.payment.dto.response;

import com.example.order.integration.payment.dto.PaymentDetailsDTO;
import com.example.order.integration.payment.dto.enums.PaymentMethod;
import com.example.order.integration.payment.dto.enums.PaymentStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentResponseMessage(
        UUID orderId,
        UUID paymentId,
        Double amount,
        PaymentMethod method,
        PaymentDetailsDTO paymentDetails,
        PaymentStatus status)
        implements Serializable {
}
