package com.example.delivery.integration.order.listaner;

import com.example.delivery.domain.Delivery;
import com.example.delivery.integration.order.dto.request.OrderPaidRequestMessage;
import com.example.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Класс слушателя Kafka для обработки сообщений о оплаченных заказах.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaidListener {

    private final DeliveryService deliveryService;

    /**
     * Метод обработки сообщения о бронировании встречи из Kafka.
     *
     * @param consumerRecord исходное сообщение Kafka
     * @param message        DTO с данными о забронированной встрече
     * @param ack            механизм подтверждения обработки сообщения
     */
    @KafkaListener(
            topics = "${kafka.service.order.order-paid-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, OrderPaidRequestMessage> consumerRecord,
                        OrderPaidRequestMessage message,
                        Acknowledgment ack) {
        log.info("Received order paid message with ID: {}", message.orderId());
        Delivery delivery = deliveryService.createDelivery(message.orderId());
        log.info("Created delivery with ID: {}", delivery.getId());

        ack.acknowledge();
        log.info("Order message successfully processed and acknowledged");
    }
}
