package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootIntegrationTest
class SpringOutboxMessageCrudRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();
    
    @Autowired
    private SpringOutboxMessageCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private OutboxMessageMapper factory;

    private final List<UUID> messageIds = new ArrayList<>();

    @BeforeEach
    void initOutboxEventFactory() {
        factory = new OutboxMessageMapper(objectMapper);
    }

    @AfterEach
    void deleteAllEvents() {
        if (!messageIds.isEmpty()) {
            repository.deleteAllById(messageIds);
        }
    }

    @Test
    void shouldFindOnlyNotPublishedOutboxEvents() {
        OutboxMessage eventOne = notPublished(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        OutboxMessage eventFour = notPublished(randomTrainingProgramProposedEvent());
        OutboxMessage eventFive = notPublished(randomTrainingProgramProposedEvent());

        List<OutboxMessage> actual = repository.findByIsPublishedFalse();

        assertThat(actual).containsExactlyInAnyOrder(eventOne, eventFour, eventFive);
    }

    private TrainingProgramProposedEvent randomTrainingProgramProposedEvent() {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        CreateTrainingProgramProposalCommand command = new CreateTrainingProgramProposalCommand(
                commandId,
                UUID.randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
        return TrainingProgramProposedEvent.create(UUID.randomUUID(), command);
    }

    private void published(TrainingProgramProposedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxMessage notPublished(TrainingProgramProposedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
        outboxMessage.published();
        messageIds.add(eventId.eventId());
        repository.save(outboxMessage);
    }

    private OutboxMessage notPublished(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
        messageIds.add(eventId.eventId());
        repository.save(outboxMessage);
        return outboxMessage;
    }
}