package com.smalaca.opentrainings.domain.eventid;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import static org.assertj.core.api.Assertions.assertThat;

public class EventIdAssertion {
    private final EventId actual;

    private EventIdAssertion(EventId actual) {
        this.actual = actual;
    }

    public static EventIdAssertion assertThatEventId(EventId actual) {
        return new EventIdAssertion(actual);
    }

    public void isNextAfter(CommandId commandId) {
        assertThat(actual.traceId()).isEqualTo(commandId.traceId());
        assertThat(actual.correlationId()).isEqualTo(commandId.correlationId());
        assertThat(actual.eventId()).isNotEqualTo(commandId.commandId());
        assertThat(actual.creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
    }
}
