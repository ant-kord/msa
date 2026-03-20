package com.example.payment.exception;

public abstract class BaseServiceException extends RuntimeException {

    public BaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
