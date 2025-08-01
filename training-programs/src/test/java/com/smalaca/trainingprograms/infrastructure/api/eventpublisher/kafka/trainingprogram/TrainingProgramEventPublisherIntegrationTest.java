package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.ExternalTrainingProgramReleasedEventAssertion.assertThatExternalTrainingProgramReleasedEvent;
import static com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.ExternalTrainingProgramRejectedEventAssertion.assertThatExternalTrainingProgramRejectedEvent;
import static com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram.RegisterProposalCommandAssertion.assertThatRegisterProposalCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.reviews.commands.register-proposal=reviews-register-proposal-command-topic",
        "kafka.topics.trainingprogram.events.training-program-released=training-program-released-event-topic",
        "kafka.topics.trainingprogram.events.training-program-rejected=training-program-rejected-event-topic"
})
@Import(TrainingProgramTestKafkaListener.class)
class TrainingProgramEventPublisherIntegrationTest {
    private static final Faker FAKER = new Faker();
    private static final UUID TRAINING_PROGRAM_PROPOSAL_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID REVIEWER_ID = UUID.randomUUID();
    private static final List<UUID> TRAINING_CATEGORIES = List.of(UUID.randomUUID(), UUID.randomUUID());
    private static final EventId EVENT_ID = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
    private static final String TRAINING_PLAN = FAKER.lorem().paragraph();
    private static final String TRAINING_AGENDA = FAKER.lorem().paragraph();
    private static final String TRAINING_DESCRIPTION = FAKER.lorem().paragraph();
    private static final String TRAINING_NAME = FAKER.company().name();

    @Autowired
    private TrainingProgramEventPublisher publisher;

    @Autowired
    private TrainingProgramTestKafkaListener listener;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishRegisterProposalCommand() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<RegisterProposalCommand> actual = listener.registerProposalCommandFor(event.trainingProgramProposalId());
            assertThat(actual).isPresent();

            assertThatRegisterProposalCommand(actual.get())
                    .hasCommandIdWithDataFrom(event.eventId())
                    .hasProposalId(TRAINING_PROGRAM_PROPOSAL_ID)
                    .hasAuthorId(AUTHOR_ID)
                    .hasTitle(TRAINING_NAME)
                    .hasCategoriesIds(TRAINING_CATEGORIES)
                    .hasContent(String.format("""
                        {
                            "name" : "%s",
                            "description" : "%s",
                            "agenda" : "%s",
                            "plan" : "%s"
                        }""", TRAINING_NAME, TRAINING_DESCRIPTION, TRAINING_AGENDA, TRAINING_PLAN));
        });
    }

    @Test
    void shouldPublishTrainingProgramReleasedEvent() {
        publisher.consume(trainingProgramReleasedEvent());

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent> actual = listener.trainingProgramReleasedEventFor(TRAINING_PROGRAM_PROPOSAL_ID);
            assertThat(actual).isPresent();

            assertThatExternalTrainingProgramReleasedEvent(actual.get())
                    .hasTrainingProgramProposalId(TRAINING_PROGRAM_PROPOSAL_ID)
                    .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                    .hasName(TRAINING_NAME)
                    .hasDescription(TRAINING_DESCRIPTION)
                    .hasAgenda(TRAINING_AGENDA)
                    .hasPlan(TRAINING_PLAN)
                    .hasAuthorId(AUTHOR_ID)
                    .hasCategoriesIds(TRAINING_CATEGORIES)
                    .hasReviewerId(REVIEWER_ID)
                    .hasEventIdWithSameDataAs(EVENT_ID);
        });
    }

    @Test
    void shouldPublishTrainingProgramRejectedEvent() {
        publisher.consume(trainingProgramRejectedEvent());

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramRejectedEvent> actual = listener.trainingProgramRejectedEventFor(TRAINING_PROGRAM_PROPOSAL_ID);
            assertThat(actual).isPresent();

            assertThatExternalTrainingProgramRejectedEvent(actual.get())
                    .hasTrainingProgramProposalId(TRAINING_PROGRAM_PROPOSAL_ID)
                    .hasReviewerId(REVIEWER_ID)
                    .hasEventIdWithSameDataAs(EVENT_ID);
        });
    }

    private TrainingProgramProposedEvent trainingProgramProposedEvent() {
        return new TrainingProgramProposedEvent(
                EVENT_ID, TRAINING_PROGRAM_PROPOSAL_ID, TRAINING_NAME, TRAINING_DESCRIPTION,
                TRAINING_AGENDA, TRAINING_PLAN, AUTHOR_ID, TRAINING_CATEGORIES);
    }

    private TrainingProgramReleasedEvent trainingProgramReleasedEvent() {
        return new TrainingProgramReleasedEvent(
                EVENT_ID, TRAINING_PROGRAM_PROPOSAL_ID, TRAINING_PROGRAM_ID, TRAINING_NAME, TRAINING_DESCRIPTION, 
                TRAINING_AGENDA, TRAINING_PLAN, AUTHOR_ID, REVIEWER_ID, TRAINING_CATEGORIES);
    }

    private TrainingProgramRejectedEvent trainingProgramRejectedEvent() {
        return new TrainingProgramRejectedEvent(
                EVENT_ID, TRAINING_PROGRAM_PROPOSAL_ID, REVIEWER_ID);
    }
}
