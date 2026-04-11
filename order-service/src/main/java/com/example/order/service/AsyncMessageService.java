package com.example.order.service;

import com.example.order.entity.AsyncMessage;

import java.util.List;

public interface AsyncMessageService {

    void saveMessage(AsyncMessage message);

    List<AsyncMessage> getUnsentOutboxMessages(int batchSize);

    void markAsSent(AsyncMessage message);
}
