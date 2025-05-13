package com.smalaca.contracts.metadata;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventIdTest {
    @Test
    void shouldCreateNewEventId() {
        LocalDateTime now = LocalDateTime.now();

        EventId eventId = EventId.newEventId();

        assertThat(eventId.eventId()).isInstanceOf(UUID.class);
        assertThat(eventId.traceId()).isInstanceOf(UUID.class);
        assertThat(eventId.correlationId()).isInstanceOf(UUID.class);
        assertThat(eventId.creationDateTime()).isAfterOrEqualTo(now);
    }

    @Test
    void shouldCreateNextCommandId() {
        EventId eventId = EventId.newEventId();

        CommandId commandId = eventId.nextCommandId();

        assertThat(commandId.commandId()).isInstanceOf(UUID.class);
        assertThat(commandId.traceId()).isEqualTo(eventId.traceId());
        assertThat(commandId.correlationId()).isEqualTo(eventId.correlationId());
        assertThat(commandId.creationDateTime()).isAfterOrEqualTo(eventId.creationDateTime());
    }
}
