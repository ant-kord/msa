package com.example.order.integration.delivery.request;

import java.util.UUID;

public record DeliveryRequestMessage(
        UUID orderId
) {
}
