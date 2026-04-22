package com.example.payment.integration.order.listener;

import com.example.payment.integration.order.dto.message.OrderCreationStatus;
import com.example.payment.integration.order.dto.message.OrderCreationStatusMessage;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Класс слушателя сообщений о статусе создания записи на оплату.
 * Обрабатывает сообщения из Kafka и обновляет статус записи в системе.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreationStatusListener {

    private final PaymentService paymentService;

    /**
     * Метод, слушающий сообщения из Kafka о статусе создания записи.
     * Обрабатывает сообщение и создает/удаляет оплату в зависимости от полученного статуса.
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
        log.info("Payment paid for orderId = " + message);
        if (message.status() == OrderCreationStatus.PENDING) {
            paymentService.createPayment(message.orderId(), message.customerId(), message.amount());
            log.info("Payment paid for orderId = " + message.orderId());

        } else if (message.status() == OrderCreationStatus.CANCEL) {

            paymentService.deleteByOrderId(String.valueOf(message.orderId()));
            log.info("Payment is deleted for orderId = " + message.orderId());
        }

        ack.acknowledge();
    }
}
