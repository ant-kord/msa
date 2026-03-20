package com.example.payment.exception;

public class IdempotencyKeyExistsException extends BaseServiceException {

    public IdempotencyKeyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
