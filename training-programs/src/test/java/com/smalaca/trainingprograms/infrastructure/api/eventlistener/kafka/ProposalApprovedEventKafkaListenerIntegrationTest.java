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
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalAssertion.assertThatTrainingProgramProposal;
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
    
    @Test
    void shouldMarkTrainingProgramProposalAsReleasedWhenProposalApprovedEventReceived() {
        UUID trainingProgramProposalId = givenExistingTrainingProgramProposal();

        kafkaTemplate.send("proposal-approved-event-topic", proposalApprovedEvent(trainingProgramProposalId));

        await().untilAsserted(() -> {
            TrainingProgramProposal proposal = repository.findById(trainingProgramProposalId);
            assertThatTrainingProgramProposal(proposal).isReleased();
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
        CommandId commandId = new CommandId(randomUUID(), randomUUID(), randomUUID(), now());
        return new CreateTrainingProgramProposalCommand(
                commandId, randomUUID(),
                "Advanced Java Programming Course",
                "This comprehensive course will teach you advanced Java programming concepts and techniques. You will learn about design patterns, concurrency, performance optimization, and modern Java features. Students will master advanced object-oriented programming principles and understand how to apply them in real-world scenarios.",
                "# Day 1: Fundamentals\n* Advanced OOP concepts\n* Design patterns overview\n* SOLID principles in practice\n\n# Day 2: Concurrency\n* Threading and synchronization\n* Concurrent collections\n* CompletableFuture and reactive programming\n\n# Day 3: Performance\n* JVM tuning and garbage collection\n* Profiling and monitoring tools\n* Memory management best practices",
                "Phase 1: Foundation Building\n1. Review core Java concepts and introduce advanced topics\n2. Hands-on exercises with design patterns\n3. Code review sessions and best practices discussion\n\nPhase 2: Advanced Topics\nStep 1: Deep dive into concurrency mechanisms\nStep 2: Practical exercises with threading\nStep 3: Performance analysis and optimization\n\nModule 3: Real-world Application\nSession 1: Project work applying learned concepts\nSession 2: Code review and feedback\nSession 3: Final presentations and wrap-up",
                List.of(randomUUID(), randomUUID()));
    }

    private void givenExistingTrainingProgramProposalFor(TrainingProgramProposedEvent event) {
        TrainingProgramProposal trainingProgramProposal = new TrainingProgramProposal(event);
        repository.save(trainingProgramProposal);
    }
}
