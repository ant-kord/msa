package com.example.order.exception;

public class SendingAsyncMessageException extends RuntimeException {

    public SendingAsyncMessageException(String message,
                                        Throwable cause) {
        super(message, cause);
    }
}
