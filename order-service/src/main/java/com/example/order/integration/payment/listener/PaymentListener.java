package com.example.order.integration.payment.listener;

import com.example.order.integration.payment.dto.response.PaymentResponseMessage;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Класс слушателя очереди сообщений RabbitMQ для обработки ответов об оплате заказа.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final OrderService orderService;

    /**
     * Обработчик сообщений из очереди RabbitMQ.
     * Обновляет статус записи на прием на основе полученного сообщения.
     *
     * @param response сообщение о резервировании времени
     */
    @RabbitListener(queues = "payment-request-queue")
    public void handle(PaymentResponseMessage response) {
        log.info("Payment response: orderId={}, status={}", response.orderId(), response.status());
        orderService.changePaymentStatus(response);
        log.info("Successfully updated order status for orderId={}", response.orderId());
    }
}
