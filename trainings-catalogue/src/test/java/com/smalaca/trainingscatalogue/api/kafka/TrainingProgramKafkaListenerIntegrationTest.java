package com.smalaca.trainingscatalogue.api.kafka;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingscatalogue.trainingprogram.JpaTrainingProgramRepository;
import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingscatalogue.trainingprogram.RandomTrainingProgramFactory.randomTrainingProgramReleasedEvent;
import static com.smalaca.trainingscatalogue.trainingprogram.TrainingProgramAssertion.assertThatTrainingProgram;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.trainingprogram.events.training-program-released=training-program-released-event-topic",
        "kafka.group-id=training-catalogue-group"
})
@Import(KafkaProducerTestConfiguration.class)
class TrainingProgramKafkaListenerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private JpaTrainingProgramRepository trainingProgramRepository;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final List<UUID> ids = new ArrayList<>();

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @AfterEach
    void deleteAll() {
        trainingProgramRepository.deleteAllById(ids);
    }

    @Test
    void shouldCreateNewTrainingProgram() {
        TrainingProgramReleasedEvent event = trainingProgramReleasedEvent();

        publish(event);

        await().untilAsserted(() -> {
            TrainingProgram actual = trainingProgramFor(event.trainingProgramId());
            assertThatTrainingProgramMatchesEvent(actual, event);
        });
    }

    @Test
    void shouldOverwriteExistingTrainingProgram() {
        TrainingProgramReleasedEvent event = trainingProgramReleasedEvent();

        publish(event);
        publish(event);

        await().untilAsserted(() -> {
            assertThat(trainingProgramRepository.findAll())
                    .hasSize(1)
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, event));
        });
    }

    @Test
    void shouldCreateTrainingPrograms() {
        TrainingProgramReleasedEvent trainingProgramReleasedEventOne = trainingProgramReleasedEvent();
        TrainingProgramReleasedEvent trainingProgramReleasedEventTwo = trainingProgramReleasedEvent();
        TrainingProgramReleasedEvent trainingProgramReleasedEventThree = trainingProgramReleasedEvent();
        TrainingProgramReleasedEvent trainingProgramReleasedEventFour = trainingProgramReleasedEvent();
        TrainingProgramReleasedEvent trainingProgramReleasedEventFive = trainingProgramReleasedEvent();

        publish(trainingProgramReleasedEventOne);
        publish(trainingProgramReleasedEventTwo);
        publish(trainingProgramReleasedEventThree);
        publish(trainingProgramReleasedEventFour);
        publish(trainingProgramReleasedEventFive);
        publish(trainingProgramReleasedEventTwo);
        publish(trainingProgramReleasedEventFive);

        await().untilAsserted(() -> {
            assertThat(trainingProgramRepository.findAll())
                    .hasSize(5)
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, trainingProgramReleasedEventOne))
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, trainingProgramReleasedEventTwo))
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, trainingProgramReleasedEventThree))
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, trainingProgramReleasedEventFour))
                    .anySatisfy(found -> assertThatTrainingProgramMatchesEvent(found, trainingProgramReleasedEventFive));
        });
    }

    private TrainingProgramReleasedEvent trainingProgramReleasedEvent() {
        TrainingProgramReleasedEvent trainingProgramReleasedEvent = randomTrainingProgramReleasedEvent();
        ids.add(trainingProgramReleasedEvent.trainingProgramId());

        return trainingProgramReleasedEvent;
    }

    private void publish(TrainingProgramReleasedEvent trainingProgramReleasedEvent) {
        kafkaTemplate.send("training-program-released-event-topic", trainingProgramReleasedEvent);
    }

    private TrainingProgram trainingProgramFor(UUID trainingProgramId) {
        return trainingProgramRepository.findById(trainingProgramId).get();
    }

    private void assertThatTrainingProgramMatchesEvent(TrainingProgram actual, TrainingProgramReleasedEvent expected) {
        assertThatTrainingProgram(actual)
                .hasTrainingProgramId(expected.trainingProgramId())
                .hasTrainingProgramProposalId(expected.trainingProgramProposalId())
                .hasAuthorId(expected.authorId())
                .hasReviewerId(expected.reviewerId())
                .hasName(expected.name())
                .hasDescription(expected.description())
                .hasAgenda(expected.agenda())
                .hasPlan(expected.plan())
                .hasCategoriesIds(expected.categoriesIds());
    }
}