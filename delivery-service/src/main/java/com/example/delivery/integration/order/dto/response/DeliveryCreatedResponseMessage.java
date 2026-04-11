package com.example.delivery.integration.order.dto.response;

import java.util.UUID;

public record DeliveryCreatedResponseMessage(
        UUID orderId,
        UUID deliveryId
) {
}
