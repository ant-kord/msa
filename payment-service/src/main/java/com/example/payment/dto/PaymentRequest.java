package com.example.payment.dto;

import com.example.payment.enums.PaymentMethod;
import com.example.payment.enums.PaymentStatus;
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
    @Schema(description = "ID заказа")
    private String orderId;
    @Schema(description = "Сумма")
    private Double amount;
    @Schema(description = "Метод оплаты")
    private PaymentMethod method;
    private PaymentDetailsDTO paymentDetails;
    private PaymentStatus status;
}
