package com.example.payment.integration.order.dto.request;

import com.example.payment.enums.PaymentMethod;
import com.example.payment.dto.PaymentDetailsDTO;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentRequestMessage(
    UUID orderId,
    Double amount,
    PaymentMethod method,
    PaymentDetailsDTO paymentDetails)
implements Serializable {
}
