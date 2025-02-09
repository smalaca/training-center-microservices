package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;

public class InvalidOutboxEventException extends RuntimeException {
    InvalidOutboxEventException(JsonProcessingException exception) {
        super(exception);
    }
}
