package com.example.payment.dto;

import com.example.payment.domain.PaymentMethod;
import com.example.payment.domain.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    private String orderId;
    private Double amount;
    private PaymentMethod method;
    private PaymentDetailsDTO paymentDetails;
    private PaymentStatus status;
}
