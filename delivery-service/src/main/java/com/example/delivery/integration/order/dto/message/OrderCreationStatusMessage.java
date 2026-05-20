package com.example.delivery.integration.order.dto.message;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record OrderCreationStatusMessage(
        UUID orderId,
        UUID paymentId,
        UUID customerId,
        BigDecimal amount,
        ZonedDateTime createdAt,
        OrderCreationStatus status
) {
}
