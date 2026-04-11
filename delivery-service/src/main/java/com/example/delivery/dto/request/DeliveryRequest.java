package com.example.delivery.dto.request;

import com.example.delivery.dto.AddressDTO;
import com.example.delivery.enums.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на создание доставки")
public class DeliveryRequest {
    private String orderId;
    private AddressDTO address;
    private DeliveryStatus status;
    private String deliveredAt;
}
