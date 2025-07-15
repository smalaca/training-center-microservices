package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.ExternalTrainingProgramReleasedEventAssertion.assertThatExternalTrainingProgramReleasedEvent;
import static com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.RegisterProposalCommandAssertion.assertThatRegisterProposalCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MessageFactoryTest {
    private static final Faker FAKER = new Faker();
    private static final UUID TRAINING_PROGRAM_PROPOSAL_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID TRAINING_CATEGORY_ONE = UUID.randomUUID();
    private static final UUID TRAINING_CATEGORY_TWO = UUID.randomUUID();
    private static final List<UUID> TRAINING_CATEGORIES = List.of(TRAINING_CATEGORY_ONE, TRAINING_CATEGORY_TWO);
    private static final EventId EVENT_ID = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
    private static final String TRAINING_PLAN = FAKER.lorem().paragraph();
    private static final String TRAINING_AGENDA = FAKER.lorem().paragraph();
    private static final String TRAINING_DESCRIPTION = FAKER.lorem().paragraph();
    private static final String TRAINING_NAME = FAKER.company().name();

    private final MessageFactory messageFactory = new MessageFactory(new ObjectMapper());

    @Test
    void shouldConvertTrainingProgramProposedEventToRegisterProposalCommand() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();

        RegisterProposalCommand actual = messageFactory.asRegisterProposalCommand(event);

        assertThatRegisterProposalCommand(actual)
                .hasCommandIdWithDataFrom(event.eventId())
                .hasProposalId(TRAINING_PROGRAM_PROPOSAL_ID)
                .hasAuthorId(AUTHOR_ID)
                .hasTitle(TRAINING_NAME)
                .hasContent(String.format("""
                        {
                            "categoriesIds" : [ "%s", "%s" ],
                            "name" : "%s",
                            "description" : "%s",
                            "agenda" : "%s",
                            "plan" : "%s"
                        }""", TRAINING_CATEGORY_ONE, TRAINING_CATEGORY_TWO, TRAINING_NAME, TRAINING_DESCRIPTION, TRAINING_AGENDA, TRAINING_PLAN));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCouldNotConvertTrainingProgramProposedEventToRegisterProposalCommand() throws JsonProcessingException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        given(objectMapper.writeValueAsString(any())).willThrow(new JsonProcessingException("Serialization error") {});
        MessageFactory messageFactory = new MessageFactory(objectMapper);

        RuntimeException actual = assertThrows(RuntimeException.class, () -> messageFactory.asRegisterProposalCommand(trainingProgramProposedEvent()));

        assertThat(actual).hasMessage("Failed to serialize content of TrainingProgramProposedEvent with trainingProgramProposalId: " + TRAINING_PROGRAM_PROPOSAL_ID);
    }

    private TrainingProgramProposedEvent trainingProgramProposedEvent() {
        return new TrainingProgramProposedEvent(
                EVENT_ID, TRAINING_PROGRAM_PROPOSAL_ID, TRAINING_NAME, TRAINING_DESCRIPTION,
                TRAINING_AGENDA, TRAINING_PLAN, AUTHOR_ID, TRAINING_CATEGORIES);
    }

    @Test
    void shouldConvertTrainingProgramReleasedEventToExternalTrainingProgramReleasedEvent() {
        TrainingProgramReleasedEvent event = trainingProgramReleasedEvent();

        com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent actual = messageFactory.asExternalTrainingProgramReleasedEvent(event);

        assertThatExternalTrainingProgramReleasedEvent(actual)
                .hasTrainingProgramProposalId(TRAINING_PROGRAM_PROPOSAL_ID)
                .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                .hasName(TRAINING_NAME)
                .hasDescription(TRAINING_DESCRIPTION)
                .hasAgenda(TRAINING_AGENDA)
                .hasPlan(TRAINING_PLAN)
                .hasAuthorId(AUTHOR_ID)
                .hasCategoriesIds(TRAINING_CATEGORIES)
                .hasEventIdWithSameDataAs(EVENT_ID);
    }

    private TrainingProgramReleasedEvent trainingProgramReleasedEvent() {
        return new TrainingProgramReleasedEvent(
                EVENT_ID, TRAINING_PROGRAM_PROPOSAL_ID, TRAINING_PROGRAM_ID, TRAINING_NAME, TRAINING_DESCRIPTION,
                TRAINING_AGENDA, TRAINING_PLAN, AUTHOR_ID, TRAINING_CATEGORIES);
    }
}