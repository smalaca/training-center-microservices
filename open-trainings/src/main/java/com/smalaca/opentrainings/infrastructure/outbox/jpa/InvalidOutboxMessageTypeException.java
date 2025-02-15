package com.smalaca.opentrainings.infrastructure.outbox.jpa;

public class InvalidOutboxMessageTypeException extends RuntimeException {
    InvalidOutboxMessageTypeException(Exception e) {
        super(e);
    }
}
