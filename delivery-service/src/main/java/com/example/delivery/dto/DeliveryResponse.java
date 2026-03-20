package com.example.delivery.dto;

import com.example.delivery.domain.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на создание доставки")
public class DeliveryResponse {
    private String id;
    private String orderId;
    private AddressDTO address;
    private DeliveryStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime deliveredAt;
}
