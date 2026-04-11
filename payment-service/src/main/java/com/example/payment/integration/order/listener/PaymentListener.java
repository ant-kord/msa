package com.example.payment.integration.order.listener;

import com.example.payment.entity.Payment;
import com.example.payment.entity.PaymentDetails;
import com.example.payment.dto.PaymentDetailsDTO;
import com.example.payment.integration.order.config.properties.RabbitMqOrderServiceProperties;
import com.example.payment.integration.order.dto.request.PaymentRequestMessage;
import com.example.payment.integration.order.dto.response.PaymentResponseMessage;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Слушатель сообщений для резервирования времени приёма.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqOrderServiceProperties properties;

    /**
     * Обрабатывает входящее сообщение с запросом оплаты.
     *
     * @param request сообщение с данными об оплате
     */
    @RabbitListener(queues = "${rabbitmq.service.order.queue-request-name}")
    public void handle(PaymentRequestMessage request) {
        log.info("Payment request={}", request);
        // Вызов сервиса для оплаты
        Payment payment = paymentService.createPayment(request);
        log.info("Payment result {}", payment);

        // Отправка ответа с результатом платежа
        PaymentResponseMessage response = PaymentResponseMessage.builder()
                .orderId(UUID.fromString(payment.getOrderId()))
                .amount(payment.getAmount())
                .paymentDetails(mapDetails(payment.getPaymentDetails()))
                .method(payment.getMethod())
                .status(payment.getStatus())
                .build();

        rabbitTemplate.convertAndSend(
                properties.exchangeResponseName(),
                properties.queueResponseName(),
                response
        );
        log.info("Sent payment response={}", response);
    }

    private PaymentDetailsDTO mapDetails(PaymentDetails paymentDetails) {
        if (paymentDetails == null) return null;
        return PaymentDetailsDTO.builder().cardLast(paymentDetails.getCardLast()).provider(paymentDetails.getProvider()).build();
    }
}
