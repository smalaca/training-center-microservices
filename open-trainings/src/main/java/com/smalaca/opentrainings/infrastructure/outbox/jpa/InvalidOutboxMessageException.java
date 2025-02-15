package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;

public class InvalidOutboxMessageException extends RuntimeException {
    InvalidOutboxMessageException(JsonProcessingException exception) {
        super(exception);
    }
}
