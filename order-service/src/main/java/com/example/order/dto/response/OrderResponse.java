package com.example.order.dto.response;

import com.example.order.enums.OrderStatus;
import com.example.order.dto.request.OrderItemRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на создание заказа")
public class OrderResponse {
    @Schema(description = "ID заказа")
    private String id;
    private String customerId;
    private List<OrderItemRequest> items;
    @Schema(description = "Сумма")
    private Double totalAmount;
    private OrderStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
