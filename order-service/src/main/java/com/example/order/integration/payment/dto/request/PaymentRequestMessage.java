package com.example.order.integration.payment.dto.request;

import com.example.order.integration.payment.dto.PaymentDetailsDTO;
import com.example.order.integration.payment.dto.enums.PaymentMethod;
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
