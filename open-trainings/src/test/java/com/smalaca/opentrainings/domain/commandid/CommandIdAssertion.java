package com.smalaca.opentrainings.domain.commandid;

import com.smalaca.opentrainings.domain.eventid.EventId;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandIdAssertion {
    private final CommandId actual;

    private CommandIdAssertion(CommandId actual) {
        this.actual = actual;
    }

    public static CommandIdAssertion assertThatCommandId(CommandId actual) {
        return new CommandIdAssertion(actual);
    }

    public CommandIdAssertion isNextAfter(EventId eventId) {
        return hasTraceIdSameAs(eventId)
                .hasCorrelationIdSameAs(eventId)
                .hasDifferentIdThan(eventId)
                .hasCreationDateTimeAfterOrEqual(eventId);
    }

    public CommandIdAssertion hasTraceIdSameAs(EventId eventId) {
        assertThat(actual.traceId()).isEqualTo(eventId.traceId());
        return this;
    }

    public CommandIdAssertion hasCorrelationIdSameAs(EventId eventId) {
        assertThat(actual.correlationId()).isEqualTo(eventId.correlationId());
        return this;
    }

    public CommandIdAssertion hasDifferentIdThan(EventId eventId) {
        assertThat(actual.commandId()).isNotEqualTo(eventId.eventId());
        return this;
    }

    public CommandIdAssertion hasCreationDateTimeAfterOrEqual(EventId eventId) {
        assertThat(actual.creationDateTime()).isAfterOrEqualTo(eventId.creationDateTime());
        return this;
    }
}
