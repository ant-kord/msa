package com.example.payment.integration.order.dto.message;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderCreationStatusMessage(
        UUID orderId,
        UUID paymentId,
        double amount,
        OrderCreationStatus status
) {
}
