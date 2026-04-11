package com.example.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание заказа")
public class OrderRequest {
    @Schema(description = "ID клиента")
    private String customerId;
    @Schema(description = "ID заказа")
    private String orderId;
    private List<OrderItemRequest> items;
}
