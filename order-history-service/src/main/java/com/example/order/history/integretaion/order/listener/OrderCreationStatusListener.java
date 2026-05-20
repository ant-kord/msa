package com.example.order.history.integretaion.order.listener;

import com.example.order.history.entity.OrderHistory;
import com.example.order.history.integretaion.order.dto.OrderCreationStatus;
import com.example.order.history.integretaion.order.dto.OrderCreationStatusMessage;
import com.example.order.history.integretaion.order.dto.PaymentMethod;
import com.example.order.history.repository.OrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderCreationStatusListener {

    private final OrderHistoryRepository orderHistoryRepository;
    private final JsonMapper mapper;

    /**
     * Метод для обработки сообщений из Kafka топика о статусе создания записи на прием.
     * Создает комплексный объект OrderHistory, содержащий в себе всю информацию о записи
     *
     * @param messageStr - сообщение в виде строки JSON
     * @param ack        - объект для ручного подтверждения обработки сообщения
     */
    @KafkaListener(
            topics = "${kafka.service.order.order-creation-status-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(String messageStr,
                        Acknowledgment ack) {
        log.info("Received message from Kafka: {}", messageStr);

        var message = mapper.readValue(messageStr, OrderCreationStatusMessage.class);
        log.debug("Parsing completed. Message: {}", message);

        UUID orderId = message.orderId();
        OrderCreationStatus status = message.status();
        OrderHistory orderHistory;

        if (status == OrderCreationStatus.PENDING) {
            orderHistory = OrderHistory.builder()
                    .id(orderId.toString())
                    .customerName(message.customerName())
                    .orderAmount(message.amount())
                    .status(status.name())
                    .build();
            log.info("Created new OrderHistory for PENDING with ID: {}", orderHistory.getId());
        } else {
            orderHistory = orderHistoryRepository.findById(orderId.toString())
                    .orElseThrow(() -> {
                        String errorMsg = "OrderHistory not found with ID: " + orderId;
                        log.error(errorMsg);
                        return new RuntimeException(errorMsg);
                    });
            log.info("Updating OrderHistory with ID: {}", orderHistory.getId());

            if (status == OrderCreationStatus.PAID) {
                PaymentMethod paymentMethod = message.paymentMethod();
                orderHistory.setPaymentMethod(paymentMethod.name());

                log.info("Updated paymentMethod: {}", message.paymentMethod());
            } else if (status == OrderCreationStatus.DELIVERY_CREATED) {
                orderHistory.setDeliveryCreatedAt(Date.from(message.createdAt().toInstant()));
                log.info("Updated deliveryCreatedAt: {}", Date.from(message.createdAt().toInstant()));
            }

            orderHistory.setStatus(status.name());
            log.info("Status updated to: {}", status.name());
        }

        orderHistoryRepository.save(orderHistory);
        log.info("OrderHistory with ID {} has been saved", orderHistory.getId());

        ack.acknowledge();
        log.info("Message acknowledged");
    }
}
