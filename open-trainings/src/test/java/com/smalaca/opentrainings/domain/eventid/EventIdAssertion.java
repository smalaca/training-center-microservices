package com.smalaca.opentrainings.domain.eventid;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.time.LocalDateTime;
import java.util.UUID;

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
        hasTraceIdSameAs(commandId.traceId())
            .hasCorrelationIdSameAs(commandId.correlationId())
            .hasDifferentIdThan(commandId.commandId())
            .hasCreationDateTimeAfterOrEqual(commandId.creationDateTime());
    }

    public void isNextAfter(EventId eventId) {
        hasTraceIdSameAs(eventId.traceId())
            .hasCorrelationIdSameAs(eventId.correlationId())
            .hasDifferentIdThan(eventId.eventId())
            .hasCreationDateTimeAfterOrEqual(eventId.creationDateTime());
    }
    
    private EventIdAssertion hasTraceIdSameAs(UUID expected) {
        assertThat(actual.traceId()).isEqualTo(expected);
        return this;
    }

    private EventIdAssertion hasCorrelationIdSameAs(UUID expected) {
        assertThat(actual.correlationId()).isEqualTo(expected);
        return this;
    }

    private EventIdAssertion hasDifferentIdThan(UUID expected) {
        assertThat(actual.eventId()).isNotEqualTo(expected);
        return this;
    }

    private EventIdAssertion hasCreationDateTimeAfterOrEqual(LocalDateTime expected) {
        assertThat(actual.creationDateTime()).isAfterOrEqualTo(expected);
        return this;
    }
}
