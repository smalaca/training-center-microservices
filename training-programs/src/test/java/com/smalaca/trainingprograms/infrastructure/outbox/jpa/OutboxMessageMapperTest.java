package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OutboxMessageMapperTest {
    private static final Faker FAKER = new Faker();
    private static final Class<TrainingProgramProposedEvent> VALID_MESSAGE_CLASS = TrainingProgramProposedEvent.class;
    private static final String VALID_MESSAGE_TYPE = VALID_MESSAGE_CLASS.getCanonicalName();
    private static final String INVALID_MESSAGE_TYPE = FAKER.code().ean8();

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OutboxMessageMapper mapper = new OutboxMessageMapper(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertEventToJson() throws JsonProcessingException {
        TrainingProgramProposedEvent event = givenTrainingProgramProposedEvent();
        JsonProcessingException exception = givenInvalidPayload(event);
        Executable executable = () -> mapper.outboxMessage(event.eventId(), event);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, executable);

        assertThat(actual).hasRootCause(exception);
    }

    private TrainingProgramProposedEvent givenTrainingProgramProposedEvent() {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        CreateTrainingProgramProposalCommand command = new CreateTrainingProgramProposalCommand(
                commandId,
                UUID.randomUUID(),
                "Test Training Program",
                "This is a test training program description",
                "Test agenda",
                "Test plan",
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
        return TrainingProgramProposedEvent.create(UUID.randomUUID(), command);
    }

    private JsonProcessingException givenInvalidPayload(Object event) throws JsonProcessingException {
        JsonProcessingException exception = new JsonGenerationException("DUMMY");
        given(objectMapper.writeValueAsString(event)).willThrow(exception);
        return exception;
    }
}