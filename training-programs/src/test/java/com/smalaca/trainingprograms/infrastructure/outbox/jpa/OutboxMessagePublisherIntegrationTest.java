package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@TestPropertySource(properties = "scheduled.outbox.message.rate=100")
class OutboxMessagePublisherIntegrationTest {
    @Autowired
    private SpringOutboxMessageCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxMessageTestListener listener;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private OutboxMessageMapper factory;

    private final List<UUID> messageIds = new ArrayList<>();

    @BeforeEach
    void initFactory() {
        factory = new OutboxMessageMapper(objectMapper);
    }

    @AfterEach
    void deleteAllEvents() {
        if (!messageIds.isEmpty()) {
            repository.deleteAllById(messageIds);
        }
    }

    @Test
    void shouldPublishOnlyNotPublishedOutboxEvents() {
        TrainingProgramProposedEvent eventOne = randomTrainingProgramProposedEvent();
        TrainingProgramProposedEvent eventTwo = randomTrainingProgramProposedEvent();
        TrainingProgramProposedEvent eventThree = randomTrainingProgramProposedEvent();
        notPublished(eventOne);
        published(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        notPublished(eventTwo);
        notPublished(eventThree);

        await()
                .untilAsserted(() -> {
                    assertThat(listener.trainingProgramProposedEvents).contains(eventOne, eventTwo, eventThree);
                });
    }

    @Test
    void shouldMarkOutboxEventsAsPublished() {
        notPublished(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        notPublished(randomTrainingProgramProposedEvent());
        notPublished(randomTrainingProgramProposedEvent());

        await()
                .untilAsserted(() -> {
                    assertThat(repository.findAll())
                            .hasSize(5)
                            .allSatisfy(actual -> assertThat(actual.isPublished()).isTrue());
                });
    }

    private TrainingProgramProposedEvent randomTrainingProgramProposedEvent() {
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

    private void published(TrainingProgramProposedEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(TrainingProgramProposedEvent event) {
        notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
            outboxMessage.published();
            messageIds.add(eventId.eventId());
            repository.save(outboxMessage);
        });
    }

    private void notPublished(EventId eventId, Object event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
            messageIds.add(eventId.eventId());
            repository.save(outboxMessage);
        });
    }
}