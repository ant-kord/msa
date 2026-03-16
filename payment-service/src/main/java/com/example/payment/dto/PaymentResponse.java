package com.example.payment.dto;

import com.example.payment.domain.PaymentMethod;
import com.example.payment.domain.PaymentStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private String orderId;
    private Double amount;
    private PaymentMethod method;
    private PaymentDetailsDTO paymentDetails;
    private PaymentStatus status;
    private OffsetDateTime createdAt;
}
