package com.example.order.history.integretaion.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record OrderCreationStatusMessage(
        UUID orderId,
        UUID paymentId,
        UUID customerId,
        String customerName,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        ZonedDateTime createdAt,
        OrderCreationStatus status
) {
}
