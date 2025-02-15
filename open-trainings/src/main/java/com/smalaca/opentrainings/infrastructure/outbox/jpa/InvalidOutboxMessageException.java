package com.smalaca.opentrainings.infrastructure.outbox.jpa;

public class InvalidOutboxMessageException extends RuntimeException {
    InvalidOutboxMessageException(Exception exception) {
        super(exception);
    }
}
