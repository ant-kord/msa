package com.example.order.integration.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание платежа")
public class PaymentRequest {

    @Schema(description = "ID заказа", example = "order-123")
    private String orderId;

    @Schema(description = "Сумма", example = "100.0")
    private Double amount;

    @Schema(description = "Метод оплаты")
    private PaymentMethod method;

    private PaymentDetailsDTO paymentDetails;

    private PaymentStatus status;
}
