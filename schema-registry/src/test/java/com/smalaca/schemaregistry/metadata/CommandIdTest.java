package com.smalaca.schemaregistry.metadata;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CommandIdTest {
    @Test
    void shouldCreateNewEventId() {
        LocalDateTime now = LocalDateTime.now();

        CommandId commandId = CommandId.newCommandId();

        assertThat(commandId.commandId()).isInstanceOf(UUID.class);
        assertThat(commandId.traceId()).isInstanceOf(UUID.class);
        assertThat(commandId.correlationId()).isInstanceOf(UUID.class);
        assertThat(commandId.creationDateTime()).isAfterOrEqualTo(now);
    }

    @Test
    void shouldCreateNextEventId() {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), java.time.LocalDateTime.now());

        EventId eventId = commandId.nextEventId();

        assertThat(eventId.eventId()).isInstanceOf(UUID.class);
        assertThat(eventId.traceId()).isEqualTo(commandId.traceId());
        assertThat(eventId.correlationId()).isEqualTo(commandId.correlationId());
        assertThat(eventId.creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
    }
}
