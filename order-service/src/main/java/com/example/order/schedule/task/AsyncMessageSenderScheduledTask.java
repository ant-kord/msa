package com.example.order.schedule.task;

import com.example.order.entity.AsyncMessage;
import com.example.order.schedule.processor.AsyncMessageSenderProcessor;
import com.example.order.service.AsyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Задача для периодической отправки асинхронных сообщений.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncMessageSenderScheduledTask {

    private final AsyncMessageService asyncMessageService;
    private final AsyncMessageSenderProcessor processor;

    /**
     * Метод, запускаемый по расписанию, который получает неподтвержденные сообщения и отправляет их.
     */
    @Scheduled(fixedDelay = 3000)
    public void sendOutboxMessages() {
        List<AsyncMessage> messages = asyncMessageService.getUnsentOutboxMessages(50);

        for (AsyncMessage message : messages) {
            processor.sendMessage(message);
        }
    }
}