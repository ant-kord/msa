package com.example.order.integration.order.dto.request;

import java.util.UUID;

public record OrderPaidRequestMessage(
        UUID orderId
) {
}
