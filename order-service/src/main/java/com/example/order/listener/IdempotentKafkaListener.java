package com.example.order.listener;

import com.example.order.entity.AsyncMessage;
import com.example.order.enums.AsyncMessageStatus;
import com.example.order.enums.AsyncMessageType;
import com.example.order.service.AsyncMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.support.Acknowledgment;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

/**
 * Абстракный класс для обработки Kafka сообщений с идемпотентным ключем
 * @param <T> тип сообщения
 */
@RequiredArgsConstructor
@Slf4j
public abstract class IdempotentKafkaListener<T> {

    private final AsyncMessageService asyncMessageService;
    private final JsonMapper jsonMapper;


    /**
     * Обработка полученного Kafka сообщения с проверкой идемпотентого ключа
     * @param message расшиврованное сообщение
     * @param record исходный Kafka рекорд
     * @param ack подтверждение обработки
     * @throws JsonProcessingException при ошибке сериализации сообщения
     */
    public void consume(T message,
                        ConsumerRecord<String, T> record,
                        Acknowledgment ack) throws JsonProcessingException {
        // Получение заголовка с ключом идемпотентности
        Header idempotentKeyHeader = record.headers().lastHeader("X-Idempotency-Key");
        if (idempotentKeyHeader == null) {
            log.error("Idempotent key header is null for consumer record {}", record);
            ack.acknowledge();
            return;
        }

        String idempotentKey = new String(idempotentKeyHeader.value(), StandardCharsets.UTF_8);
        log.info("Idempotent key : {}", idempotentKey);

        AsyncMessage asyncMessage = AsyncMessage.builder()
                .id(idempotentKey)
                .topic(record.topic())
                .value(jsonMapper.writeValueAsString(message))
                .status(AsyncMessageStatus.RECEIVED)
                .type(AsyncMessageType.INBOX)
                .build();

        try {
            // Сохранение сообщения для проверки идемпотентности
            asyncMessageService.saveMessage(asyncMessage);
        } catch (DataIntegrityViolationException e) {
            log.warn("Message with idempotentKey {} already exists", idempotentKey);
            ack.acknowledge();
            return;
        }

        processConsumedMessage(message);
        ack.acknowledge();

    }

    public abstract void processConsumedMessage(T message);
}
