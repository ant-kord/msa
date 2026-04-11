package com.example.order.integration.delivery.dto.request;

import java.util.UUID;

public record OrderPaidRequestMessage(
        UUID orderId
) {
}
