package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalReleaseFailedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.infrastructure.api.eventlistener.spring.TrainingProgramProposalEventListener;
import com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.TrainingProgramEventPublisher;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    private static final Faker FAKER = new Faker();
    @Autowired
    private SpringOutboxMessageCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxMessageTestListener listener;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private TrainingProgramEventPublisher trainingProgramEventPublisher;

    @MockBean
    private TrainingProgramProposalEventListener trainingProgramProposalEventListener;

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
        TrainingProgramReleasedEvent eventFour = randomTrainingProgramReleasedEvent();
        TrainingProgramReleasedEvent eventFive = randomTrainingProgramReleasedEvent();
        TrainingProgramProposalReleaseFailedEvent eventSix = randomTrainingProgramProposalReleaseFailedEvent();
        TrainingProgramProposalReleaseFailedEvent eventSeven = randomTrainingProgramProposalReleaseFailedEvent();
        TrainingProgramRejectedEvent eventEight = randomTrainingProgramRejectedEvent();
        TrainingProgramRejectedEvent eventNine = randomTrainingProgramRejectedEvent();
        notPublished(eventOne);
        published(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        notPublished(eventTwo);
        notPublished(eventThree);
        notPublished(eventFour);
        published(randomTrainingProgramReleasedEvent());
        notPublished(eventFive);
        notPublished(eventSix);
        published(randomTrainingProgramProposalReleaseFailedEvent());
        notPublished(eventSeven);
        notPublished(eventEight);
        published(randomTrainingProgramRejectedEvent());
        notPublished(eventNine);

        await()
                .untilAsserted(() -> {
                    assertThat(listener.trainingProgramProposedEvents).contains(eventOne, eventTwo, eventThree);
                    assertThat(listener.trainingProgramReleasedEvents).contains(eventFour, eventFive);
                    assertThat(listener.trainingProgramProposalReleaseFailedEvents).contains(eventSix, eventSeven);
                    assertThat(listener.trainingProgramRejectedEvents).contains(eventEight, eventNine);
                });
    }

    @Test
    void shouldMarkOutboxEventsAsPublished() {
        notPublished(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        published(randomTrainingProgramProposedEvent());
        notPublished(randomTrainingProgramProposedEvent());
        notPublished(randomTrainingProgramProposedEvent());
        notPublished(randomTrainingProgramReleasedEvent());
        published(randomTrainingProgramReleasedEvent());
        notPublished(randomTrainingProgramReleasedEvent());
        notPublished(randomTrainingProgramProposalReleaseFailedEvent());
        published(randomTrainingProgramProposalReleaseFailedEvent());
        notPublished(randomTrainingProgramRejectedEvent());
        published(randomTrainingProgramRejectedEvent());

        await()
                .untilAsserted(() -> {
                    assertThat(repository.findAll())
                            .hasSize(12)
                            .allSatisfy(actual -> assertThat(actual.isPublished()).isTrue());
                });
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

    private TrainingProgramReleasedEvent randomTrainingProgramReleasedEvent() {
        return TrainingProgramReleasedEvent.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                FAKER.book().title(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                FAKER.lorem().paragraph(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    private TrainingProgramProposalReleaseFailedEvent randomTrainingProgramProposalReleaseFailedEvent() {
        return TrainingProgramProposalReleaseFailedEvent.create(
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private TrainingProgramRejectedEvent randomTrainingProgramRejectedEvent() {
        return TrainingProgramRejectedEvent.create(
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private void published(TrainingProgramProposalEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(TrainingProgramProposalEvent event) {
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
