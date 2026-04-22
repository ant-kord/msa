package com.example.delivery.integration.order.listener;

import com.example.delivery.integration.order.dto.message.OrderCreationStatus;
import com.example.delivery.integration.order.dto.message.OrderCreationStatusMessage;
import com.example.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Класс слушателя сообщений о статусе создания записи на прием.
 * Обрабатывает сообщения из Kafka и обновляет статус записи в системе.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreationStatusListener {

    private final DeliveryService deliveryService;

    /**
     * Метод, слушающий сообщения из Kafka о статусе создания записи.
     * Обрабатывает сообщение и создает/удаляет доставку в зависимости от полученного статуса.
     *
     * @param message Полученное сообщение с информацией о статусе
     * @param ack     Объект для подтверждения обработки сообщения
     */
    @KafkaListener(
            topics = "${kafka.service.order.order-creation-status-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderCreationStatusMessage message,
                        Acknowledgment ack) {

        if (message.status() == OrderCreationStatus.PENDING) {
            deliveryService.createDelivery(message.orderId());
            log.info("Delivery created for orderId = " + message.orderId());

        } else if (message.status() == OrderCreationStatus.CANCEL) {

            deliveryService.deleteByOrderId(String.valueOf(message.orderId()));
            log.info("Delivery is deleted for orderId = " + message.orderId());
        }

        ack.acknowledge();
    }
}
