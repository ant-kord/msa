package com.example.payment.dto.response;

import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.enums.PaymentMethod;
import com.example.payment.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на создание платежа")
public class PaymentResponse {
    private String id;
    @Schema(description = "ID заказа")
    private String orderId;
    @Schema(description = "Сумма")
    private Double amount;
    @Schema(description = "Метод оплаты")
    private PaymentMethod method;
    private PaymentDetailsDTO paymentDetails;
    @Schema(description = "Статус")
    private PaymentStatus status;
    private OffsetDateTime createdAt;
}
