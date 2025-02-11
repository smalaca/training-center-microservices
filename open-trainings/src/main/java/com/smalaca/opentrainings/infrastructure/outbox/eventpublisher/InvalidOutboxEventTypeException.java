package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

public class InvalidOutboxEventTypeException extends RuntimeException {
    InvalidOutboxEventTypeException(Exception e) {
        super(e);
    }
}
