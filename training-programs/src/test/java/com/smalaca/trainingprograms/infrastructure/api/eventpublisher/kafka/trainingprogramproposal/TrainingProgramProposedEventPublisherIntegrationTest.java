package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogramproposal;

import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.eventid.EventId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import(TrainingProgramProposedTestKafkaListener.class)
class TrainingProgramProposedEventPublisherIntegrationTest {

    private final Faker faker = new Faker();

    @Autowired
    private TrainingProgramProposedEventPublisher publisher;

    @Autowired
    private TrainingProgramProposedTestKafkaListener consumer;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingProgramProposedEvent() {
        TrainingProgramProposedEvent event = randomTrainingProgramProposedEvent();

        publisher.consume(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent> actual = consumer.trainingProgramProposedEventFor(event.trainingProgramProposalId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), event);
        });
    }

    private TrainingProgramProposedEvent randomTrainingProgramProposedEvent() {
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String name = faker.company().name();
        String description = faker.lorem().paragraph();
        String agenda = faker.lorem().paragraph();
        String plan = faker.lorem().paragraph();
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        
        EventId eventId = new EventId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), faker.date().birthday().toLocalDateTime());
        
        return new TrainingProgramProposedEvent(
                eventId, trainingProgramProposalId, name, description, agenda, plan, authorId, categoriesIds);
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposedEvent actual, TrainingProgramProposedEvent expected) {
        assertThatContainsSameData(actual.eventId(), expected.eventId());
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected.trainingProgramProposalId());
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