package com.smalaca.trainingprograms.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.EventId;
import com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent;
import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalRepository;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.commands.CreateTrainingProgramProposalCommand;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;
import com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogram.SpringTrainingProgramCrudRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.reviews.events.proposal-approved=proposal-approved-event-topic"
})
@Import(TrainingProgramReleasedEventTestKafkaListener.class)
class ProposalApprovedEventKafkaListenerIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private TrainingProgramReleasedEventTestKafkaListener consumer;

    @Autowired
    private TrainingProgramProposalRepository repository;

    @Autowired
    private SpringTrainingProgramCrudRepository trainingProgramRepository;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingProgramReleasedEventWhenProposalApprovedEventReceived() {
        UUID trainingProgramProposalId = givenExistingTrainingProgramProposal();

        kafkaTemplate.send("proposal-approved-event-topic", proposalApprovedEvent(trainingProgramProposalId));

        await().untilAsserted(() -> {
            Optional<TrainingProgramReleasedEvent> found = consumer.trainingProgramReleasedEventFor(trainingProgramProposalId);
            assertThat(found).isPresent();

            TrainingProgramReleasedEvent actual = found.get();
            assertThat(actual.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
            assertThat(actual.trainingProgramId()).isNotNull();
        });
    }

    @Test
    void shouldCreateTrainingProgramWhenProposalApprovedEventReceived() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();
        givenExistingTrainingProgramProposalFor(event);

        kafkaTemplate.send("proposal-approved-event-topic", proposalApprovedEvent(event.trainingProgramProposalId()));

        await().untilAsserted(() -> {
            assertThat(trainingProgramRepository.findAll())
                    .anySatisfy(actual -> assertThatTrainingProgram(actual)
                            .hasTrainingProgramIdNotNull()
                            .hasTrainingProgramProposalId(event.trainingProgramProposalId())
                            .hasName(event.name())
                            .hasDescription(event.description())
                            .hasAgenda(event.agenda())
                            .hasPlan(event.plan())
                            .hasAuthorId(event.authorId())
                            .hasCategoriesIds(event.categoriesIds()));

        });
    }

    private ProposalApprovedEvent proposalApprovedEvent(UUID trainingProgramProposalId) {
        return new ProposalApprovedEvent(EventId.newEventId(), trainingProgramProposalId, randomUUID());
    }

    private UUID givenExistingTrainingProgramProposal() {
        TrainingProgramProposedEvent event = trainingProgramProposedEvent();
        givenExistingTrainingProgramProposalFor(event);

        return event.trainingProgramProposalId();
    }

    private TrainingProgramProposedEvent trainingProgramProposedEvent() {
        return TrainingProgramProposedEvent.create(randomUUID(), createTrainingProgramProposalCommand());
    }

    private CreateTrainingProgramProposalCommand createTrainingProgramProposalCommand() {
        return new CreateTrainingProgramProposalCommand(
                new CommandId(randomUUID(), randomUUID(), randomUUID(), now()),
                randomUUID(), FAKER.book().title(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(), FAKER.lorem().paragraph(),
                List.of(randomUUID(), randomUUID()));
    }

    private void givenExistingTrainingProgramProposalFor(TrainingProgramProposedEvent event) {
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);
        repository.save(trainingProgramProposal);
    }
}
