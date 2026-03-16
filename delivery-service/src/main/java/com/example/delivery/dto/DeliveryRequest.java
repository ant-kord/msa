package com.example.delivery.dto;

import com.example.delivery.domain.DeliveryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequest {
    private String orderId;
    private AddressDTO address;
    private DeliveryStatus status;
    private String deliveredAt;
}
