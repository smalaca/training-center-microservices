package com.smalaca.discountmanagement.api;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.metadata.EventId;

import static org.assertj.core.api.Assertions.assertThat;

class EventIdAssertion {
    private final EventId actual;

    private EventIdAssertion(EventId actual) {
        this.actual = actual;
    }

    static EventIdAssertion assertThatEventId(EventId actual) {
        return new EventIdAssertion(actual);
    }

    void isNextAfter(CommandId commandId) {
        assertThat(actual.traceId()).isEqualTo(commandId.traceId());
        assertThat(actual.correlationId()).isEqualTo(commandId.correlationId());
        assertThat(actual.eventId()).isNotEqualTo(commandId.commandId());
        assertThat(actual.creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
    }
}