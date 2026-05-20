package com.example.order.saga;

import com.example.order.dto.message.OrderCreationStatus;
import com.example.order.dto.message.OrderCreationStatusMessage;
import com.example.order.integration.delivery.request.DeliveryRequestMessage;
import com.example.order.integration.payment.dto.request.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreationSagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "${kafka.service.order.order-creation-status-topic}",
            groupId = "saga-group"
    )
    public void handleSagaEvents(OrderCreationStatusMessage message, Acknowledgment acknowledgment) {
        var orderId = message.orderId();
        var status = message.status();

        log.info("Received saga event for orderId: {}, status: {}", orderId, status);

        log.info("Saga event received: {}", message);
        switch (status) {
            case PENDING-> {
                log.info("Handling PENDING status, sending paid reservation for orderId: {}", orderId);
                sendPaymentCreateMessage(message, orderId);
            }

            case PAID -> {
                log.info("Paid completed, sending insurance create message for orderId: {}", orderId);
                sendDeliveryCreateMessage(orderId);
            }

            case PAID_ERROR -> {
                log.info("Paid error, sending cancel order for orderId: {}", orderId);
                sendCancelOrderMessage(orderId);
            }

            case DELIVERY_CREATED -> {
                log.info("Order created, sending completed order message for orderId: {}", orderId);
                sendCompletedOrderMessage(orderId);
            }

            case DELIVERY_ERROR -> {
                log.warn("Delivery error for orderId: {}, releasing payment and cancelling order", orderId);
                sendCancelPaidMessage(orderId);
                sendCancelOrderMessage(orderId);
            }

            default -> log.warn("Unknown status: {} for orderId: {}", status, orderId);
        }

        acknowledgment.acknowledge();
    }

    private void sendPaymentCreateMessage(OrderCreationStatusMessage message,
                                          UUID orderId) {
        var paidReservation = PaymentRequestMessage.builder()
                .orderId(orderId)
                .customerId(message.customerId())
                .amount(message.amount().doubleValue())
                .build();

        kafkaTemplate.send("payment.create.request", paidReservation);
    }

    private void sendDeliveryCreateMessage(UUID orderId) {
        var deliveryMessage = new DeliveryRequestMessage(orderId);
        kafkaTemplate.send("delivery.create.request", deliveryMessage);
    }

    private void sendCancelOrderMessage(UUID orderId) {
        var statusMessage = OrderCreationStatusMessage.builder()
                .orderId(orderId)
                .status(OrderCreationStatus.CANCELLED)
                .build();
        kafkaTemplate.send("order.creation.status", statusMessage);
    }

    private void sendCompletedOrderMessage(UUID orderId) {
        var statusMessage = OrderCreationStatusMessage.builder()
                .orderId(orderId)
                .status(OrderCreationStatus.COMPLETED)
                .build();
        kafkaTemplate.send("order.creation.status", statusMessage);
    }

    private void sendCancelPaidMessage(UUID orderId) {
        var paidCancel = PaymentRequestMessage.builder()
                .orderId(orderId)
                .build();
        kafkaTemplate.send("payment.remove.request", paidCancel);
    }

}
