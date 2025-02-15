package com.smalaca.opentrainings.infrastructure.outbox.jpa;

public class InvalidOutboxEventTypeException extends RuntimeException {
    InvalidOutboxEventTypeException(Exception e) {
        super(e);
    }
}
