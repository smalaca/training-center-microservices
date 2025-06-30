package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

public class InvalidOutboxMessageException extends RuntimeException {
    InvalidOutboxMessageException(Exception exception) {
        super(exception);
    }
}