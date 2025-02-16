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

    public EventIdAssertion isNextAfter(CommandId commandId) {
        return hasTraceIdSameAs(commandId)
            .hasCorrelationIdSameAs(commandId)
            .hasDifferentIdThan(commandId)
            .hasCreationDateTimeAfterOrEqual(commandId);
    }
    
    public EventIdAssertion hasTraceIdSameAs(CommandId commandId) {
        assertThat(actual.traceId()).isEqualTo(commandId.traceId());
        return this;
    }

    public EventIdAssertion hasCorrelationIdSameAs(CommandId commandId) {
        assertThat(actual.correlationId()).isEqualTo(commandId.correlationId());
        return this;
    }

    public EventIdAssertion hasDifferentIdThan(CommandId commandId) {
        assertThat(actual.eventId()).isNotEqualTo(commandId.commandId());
        return this;
    }

    public EventIdAssertion hasCreationDateTimeAfterOrEqual(CommandId commandId) {
        assertThat(actual.creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
        return this;
    }
}
