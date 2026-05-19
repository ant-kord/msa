package com.example.delivery.integration.order.listener;


import com.example.delivery.integration.order.dto.request.DeliveryRequestMessage;
import com.example.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCommandListener {


    private final DeliveryService deliveryService;

    @KafkaListener(
            topics = "${kafka.service.delivery.delivery-create-request}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeReservationRequest(DeliveryRequestMessage message) {
        log.info("Received insurance confirmation creation request for orderId: {}", message.orderId());
        deliveryService.createDelivery(message.orderId());
    }

    @KafkaListener(
            topics = "${kafka.service.delivery.delivery-delete-request}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeReleaseRequest(DeliveryRequestMessage message) {
        log.info("Received insurance confirmation deletion request for orderId: {}", message.orderId());
        deliveryService.deleteByOrderId(message.orderId().toString());
    }
}
