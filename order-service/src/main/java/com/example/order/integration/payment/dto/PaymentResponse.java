package com.example.order.integration.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ с информацией о платеже")
public class PaymentResponse {
    private String id;
    private String orderId;
    private Double amount;
    private PaymentMethod method;
    private PaymentDetailsDTO paymentDetails;
    private PaymentStatus status;
    private OffsetDateTime createdAt;
}
