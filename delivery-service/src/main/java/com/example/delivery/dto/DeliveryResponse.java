package com.example.delivery.dto;

import com.example.delivery.domain.DeliveryStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private String id;
    private String orderId;
    private AddressDTO address;
    private DeliveryStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime deliveredAt;
}
