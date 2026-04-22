package com.example.order.schedule.processor;

import com.example.order.dto.message.OrderCreationStatusMessage;
import com.example.order.entity.AsyncMessage;
import com.example.order.exception.SendingAsyncMessageException;
import com.example.order.integration.delivery.dto.request.OrderPaidRequestMessage;
import com.example.order.service.AsyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Класс для обработки и отправки асинхронных сообщений через Kafka.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncMessageSenderProcessor {

    private final AsyncMessageService asyncMessageService;
    private final KafkaTemplate<String, OrderCreationStatusMessage> kafkaTemplate;
    private final JsonMapper mapper;

    /**
     * Отправляет асинхронное сообщение через Kafka и обновляет его статус.
     * Оборачивается в транзакцию для обеспечения атомарности.
     *
     * @param message сообщение, которое необходимо отправить
     */
    @Transactional
    public void sendMessage(AsyncMessage message) {
        try {
            var reqMessage = mapper.readValue(message.getValue(), OrderCreationStatusMessage.class);

            kafkaTemplate.send(message.getTopic(), message.getId().getId(), reqMessage)
                .exceptionally(e -> {
                    throw new SendingAsyncMessageException("Error on sending message '%s'".formatted(message), e);
                })
                .get();

            asyncMessageService.markAsSent(message);
        } catch (Exception e) {
            throw new SendingAsyncMessageException("Error on sending message '%s'".formatted(message), e);
        }
    }
}