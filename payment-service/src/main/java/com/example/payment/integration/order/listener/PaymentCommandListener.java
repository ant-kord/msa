package com.example.payment.integration.order.listener;

import com.example.payment.integration.order.dto.request.PaymentRequestMessage;
import com.example.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandListener {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${kafka.service.payment.payment-create-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeReservationRequest(PaymentRequestMessage message) {
        log.info("Received timeslot reservation request for orderId: {}, customerId: {}, amount: {},",
                message.orderId(), message.customerId(), message.amount());
        paymentService.createPayment(message.orderId(), message.customerId(), message.amount());
    }

    @KafkaListener(
            topics = "${kafka.service.payment.payment-remove-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeReleaseRequest(PaymentRequestMessage message) {
        log.info("Received timeslot release request for orderId: {}", message.orderId());
        paymentService.deleteByOrderId(message.orderId().toString());
    }
}
