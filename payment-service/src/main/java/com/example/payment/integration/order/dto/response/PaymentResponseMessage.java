package com.example.payment.integration.order.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentResponseMessage(
        UUID orderId,
        PaymentStatus status)
        implements Serializable {
}
