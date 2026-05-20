package com.example.delivery.integration.order.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryRequestMessage(
        UUID orderId
) {
}
