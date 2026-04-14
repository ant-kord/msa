package com.example.order.integration.delivery.dto.response;

import java.util.UUID;

public record DeliveryCreatedResponseMessage(
        UUID orderId,
        UUID deliveryId
) {
}
