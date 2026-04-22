package com.example.order.integration.delivery.listener;

import com.example.order.integration.delivery.dto.response.DeliveryCreatedResponseMessage;
import com.example.order.listener.IdempotentKafkaListener;
import com.example.order.service.AsyncMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

//@Component
//@Slf4j
public class DeliveryCreatedListener extends IdempotentKafkaListener<DeliveryCreatedResponseMessage> {


    public DeliveryCreatedListener(AsyncMessageService asyncMessageService, JsonMapper jsonMapper) {
        super(asyncMessageService, jsonMapper);
    }

    /*@KafkaListener(
            topics = "${kafka.service.delivery.delivery-created-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )*/
    @Override
    public void consume(DeliveryCreatedResponseMessage message,
                        ConsumerRecord<String, DeliveryCreatedResponseMessage> record,
                        Acknowledgment ack) throws JsonProcessingException {
        //log.info("Received delivery created event");
        super.consume(message, record, ack);
    }

    @Transactional
    @Override
    public void processConsumedMessage(DeliveryCreatedResponseMessage message) {
        //log.info("Consumed delivery created response message: " + message);
    }
}
