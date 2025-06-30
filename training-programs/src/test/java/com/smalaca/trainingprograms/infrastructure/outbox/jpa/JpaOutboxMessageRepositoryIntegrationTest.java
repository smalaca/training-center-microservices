package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.infrastructure.outbox.jpa.OutboxMessageAssertion.assertThatOutboxMessage;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootIntegrationTest
@Import(JpaOutboxMessageRepositoryFactory.class)
class JpaOutboxMessageRepositoryIntegrationTest {

    @Autowired
    private JpaOutboxMessageRepository repository;

    @Autowired
    private SpringOutboxMessageCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final List<UUID> messagesIds = new ArrayList<>();

    @AfterEach
    void deleteAllEvents() {
        if (!messagesIds.isEmpty()) {
            springRepository.deleteAllById(messagesIds);
        }
    }

    @Test
    void shouldFindAllOutboxMessages() {
        TrainingProgramProposedEvent trainingProgramProposedEvent = publish(randomTrainingProgramProposedEvent());

        assertThat(springRepository.findAll())
                .anySatisfy(actual -> assertTrainingProgramProposedEventSaved(actual, trainingProgramProposedEvent));
    }

    private <T extends TrainingProgramProposedEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
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

    private void assertTrainingProgramProposedEventSaved(OutboxMessage actual, TrainingProgramProposedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }
}