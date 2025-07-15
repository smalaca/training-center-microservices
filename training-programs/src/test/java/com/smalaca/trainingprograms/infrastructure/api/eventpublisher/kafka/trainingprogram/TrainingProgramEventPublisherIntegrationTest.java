package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.reviews.commands.RegisterProposalCommand;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.reviews.commands.register-proposal=reviews-register-proposal-command-topic",
        "kafka.topics.trainingprogram.events.training-program-released=training-program-released-event-topic"
})
@Import(TrainingProgramTestKafkaListener.class)
class TrainingProgramEventPublisherIntegrationTest {
    private static final Faker FAKER = new Faker();

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
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<RegisterProposalCommand> actual = listener.registerProposalCommandFor(event.trainingProgramProposalId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    @Test
    void shouldPublishTrainingProgramReleasedEvent() {
        TrainingProgramReleasedEvent event = randomTrainingProgramReleasedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent> actual = listener.trainingProgramReleasedEventFor(event.trainingProgramProposalId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    private TrainingProgramProposedEvent randomTrainingProgramProposedEvent() {
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String name = FAKER.company().name();
        String description = FAKER.lorem().paragraph();
        String agenda = FAKER.lorem().paragraph();
        String plan = FAKER.lorem().paragraph();
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        EventId eventId = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        return new TrainingProgramProposedEvent(
                eventId, trainingProgramProposalId, name, description, agenda, plan, authorId, categoriesIds);
    }

    private TrainingProgramReleasedEvent randomTrainingProgramReleasedEvent() {
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID trainingProgramId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String name = FAKER.company().name();
        String description = FAKER.lorem().paragraph();
        String agenda = FAKER.lorem().paragraph();
        String plan = FAKER.lorem().paragraph();
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        EventId eventId = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        return new TrainingProgramReleasedEvent(
                eventId, trainingProgramProposalId, trainingProgramId, name, description, agenda, plan, authorId, categoriesIds);
    }

    private void assertThatContainsSameData(RegisterProposalCommand actual, TrainingProgramProposedEvent expected) {
        assertThat(actual.proposalId()).isEqualTo(expected.trainingProgramProposalId());
        assertThat(actual.authorId()).isEqualTo(expected.authorId());
        assertThat(actual.title()).isEqualTo(expected.name());

        String content = actual.content();
        assertThat(content).contains(expected.name());
        assertThat(content).contains(expected.description());
        assertThat(content).contains(expected.agenda());
        assertThat(content).contains(expected.plan());
        for (UUID categoryId : expected.categoriesIds()) {
            assertThat(content).contains(categoryId.toString());
        }

        assertThat(actual.commandId().commandId()).isNotNull();
        assertThat(actual.commandId().creationDateTime()).isNotNull();
        assertThat(actual.commandId().traceId()).isEqualTo(expected.eventId().traceId());
        assertThat(actual.commandId().correlationId()).isEqualTo(expected.eventId().correlationId());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent actual, TrainingProgramReleasedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected.trainingProgramProposalId());
        assertThat(actual.trainingProgramId()).isEqualTo(expected.trainingProgramId());
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.agenda()).isEqualTo(expected.agenda());
        assertThat(actual.plan()).isEqualTo(expected.plan());
        assertThat(actual.authorId()).isEqualTo(expected.authorId());
        assertThat(actual.categoriesIds()).isEqualTo(expected.categoriesIds());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.metadata.EventId actual, EventId expected) {
        assertThat(actual.eventId()).isEqualTo(expected.eventId());
        assertThat(actual.traceId()).isEqualTo(expected.traceId());
        assertThat(actual.correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.creationDateTime()).isEqualTo(expected.creationDateTime());
    }
}
