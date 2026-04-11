package com.example.payment.service;

import com.example.payment.entity.IdempotencyKey;

import java.util.Optional;

public interface IdempotencyService {

    void createPendingKey(String key);

    Optional<IdempotencyKey> getByKey(String key);

    void markAsCompleted(String key, String responseData, int statusCode);
}
