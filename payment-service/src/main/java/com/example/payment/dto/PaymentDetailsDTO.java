package com.example.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailsDTO {
    private String cardLast;
    private String provider;
}
