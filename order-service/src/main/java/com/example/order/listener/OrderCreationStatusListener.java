package com.example.order.listener;

import com.example.order.dto.message.OrderCreationStatus;
import com.example.order.dto.message.OrderCreationStatusMessage;
import com.example.order.enums.OrderStatus;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Класс слушателя сообщений о статусе создания записи на оплату.
 * Обрабатывает сообщения из Kafka и обновляет статус записи в системе.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreationStatusListener {

    /**
     * Набор статусов ошибок, при которых происходит отмена записи.
     */
    private static final EnumSet<OrderCreationStatus> ERROR_STATUS =
            EnumSet.of(OrderCreationStatus.DELIVERY_ERROR, OrderCreationStatus.PAID_ERROR);


    private final OrderService orderService;

    /**
     * Метод, слушающий сообщения из Kafka о статусе создания записи.
     * Обрабатывает сообщение и создает/удаляет заказ в зависимости от полученного статуса.
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

        OrderCreationStatus status = message.status();

        log.info("Received order creation message: {}", message);

        if (status == OrderCreationStatus.PAID) {
            orderService.changeOrderStatus(message.orderId(), OrderStatus.PAID);
            log.info("Order status message '%s' and changed order status to '%s'"
                    .formatted(status, OrderStatus.PAID));

        } else if (status == OrderCreationStatus.DELIVERY_CREATED) {
            orderService.changeOrderStatus(message.orderId(), OrderStatus.CONFIRMED);
            log.info("Order status message '%s' and changed order status to '%s'"
                    .formatted(status, OrderStatus.CONFIRMED));
        } else if (ERROR_STATUS.contains(status)) {
            orderService.cancelOrder(message.orderId());
            log.info("Order status message '%s' and cancelled order".formatted(status));
        }

        ack.acknowledge();
    }
}
