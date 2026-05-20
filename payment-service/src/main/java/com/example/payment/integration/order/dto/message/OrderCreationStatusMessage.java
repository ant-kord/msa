package com.example.payment.integration.order.dto.message;

import com.example.payment.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderCreationStatusMessage(
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        OrderCreationStatus status
) {
}
