package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.eventid.EventId;

class OutboxMessageMapper {
    private final ObjectMapper objectMapper;

    OutboxMessageMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OutboxMessage outboxMessage(CommandId commandId, Object command) {
        return new OutboxMessage(
                commandId.commandId(),
                commandId.creationDateTime(),
                command.getClass().getCanonicalName(),
                asPayload(command));
    }

    OutboxMessage outboxMessage(EventId eventId, Object event) {
        return new OutboxMessage(
                eventId.eventId(),
                eventId.creationDateTime(),
                event.getClass().getCanonicalName(),
                asPayload(event));
    }

    private String asPayload(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException exception) {
            throw new InvalidOutboxMessageException(exception);
        }
    }

    Object message(OutboxMessage outboxMessage) {
        try {
            return objectMapper.readValue(outboxMessage.getPayload(), Class.forName(outboxMessage.getMessageType()));
        } catch (JsonProcessingException | ClassNotFoundException exception) {
            throw new InvalidOutboxMessageException(exception);
        }
    }
}
