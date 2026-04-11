package com.example.order.service.impl;

import com.example.order.entity.AsyncMessage;
import com.example.order.enums.AsyncMessageStatus;
import com.example.order.repository.AsyncMessageRepository;
import com.example.order.service.AsyncMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncMessageServiceImpl implements AsyncMessageService {

    private final AsyncMessageRepository asyncMessageRepository;

    /**
     * Сохраняет сообщение в базе данных.
     *
     * @param message сообщение для сохранения
     */
    @Override
    @Transactional
    public void saveMessage(AsyncMessage message) {
        asyncMessageRepository.save(message);
    }

    /**
     * Получает список неподтвержденных (неотправленных) сообщений с ограничением по размеру батча.
     *
     * @param batchSize максимальное количество сообщений
     * @return список неподтвержденных сообщений
     */
    @Override
    public List<AsyncMessage> getUnsentOutboxMessages(int batchSize) {
        Pageable pageable = Pageable.ofSize(batchSize);
        return asyncMessageRepository.findUnsentOutboxMessages(pageable);
    }

    /**
     * Обновляет статус сообщения на "Отправлено" и сохраняет изменение.
     *
     * @param message сообщение, которое нужно пометить как отправленное
     */
    @Override
    public void markAsSent(AsyncMessage message) {
        message.setStatus(AsyncMessageStatus.SENT);
        asyncMessageRepository.save(message);
    }
}
