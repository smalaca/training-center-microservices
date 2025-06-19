package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OutboxMessageMapperTest {
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OutboxMessageMapper mapper = new OutboxMessageMapper(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertEventToJson() throws JsonProcessingException {
        TrainingOfferPublishedEvent event = givenTrainingOfferPublishedEvent();
        JsonProcessingException exception = givenInvalidPayload(event);
        Executable executable = () -> mapper.outboxMessage(event.eventId(), event);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, executable);

        assertThat(actual).hasRootCause(exception);
    }

    private TrainingOfferPublishedEvent givenTrainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100.00), "USD", 5, 20,
                LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(17, 0));
    }

    private JsonProcessingException givenInvalidPayload(Object event) throws JsonProcessingException {
        JsonProcessingException exception = new JsonGenerationException("DUMMY");
        given(objectMapper.writeValueAsString(event)).willThrow(exception);
        return exception;
    }
}