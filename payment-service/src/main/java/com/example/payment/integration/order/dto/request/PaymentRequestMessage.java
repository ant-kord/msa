package com.example.payment.integration.order.dto.request;

import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.enums.PaymentMethod;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentRequestMessage(
    UUID orderId,
    UUID customerId,
    Double amount,
    PaymentMethod method,
    PaymentDetailsDTO paymentDetails)
implements Serializable {
}
