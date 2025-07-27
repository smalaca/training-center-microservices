package com.smalaca.trainingscatalogue.api.kafka;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingscatalogue.trainingoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingscatalogue.trainingoffer.TrainingOffer;
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

import static com.smalaca.trainingscatalogue.trainingoffer.RandomTrainingOfferFactory.randomTrainingOfferPublishedEvent;
import static com.smalaca.trainingscatalogue.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.trainingoffer.events.training-offer-published=training-offer-published-event-topic",
        "kafka.group-id=training-catalogue-group"
})
@Import(KafkaProducerTestConfiguration.class)
class TrainingOfferKafkaListenerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private JpaTrainingOfferRepository trainingOfferRepository;

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
        trainingOfferRepository.deleteAllById(ids);
    }

    @Test
    void shouldCreateNewTrainingOffer() {
        TrainingOfferPublishedEvent event = trainingOfferPublishedEvent();

        publish(event);

        await().untilAsserted(() -> {
            TrainingOffer actual = trainingOfferFor(event.trainingOfferId());
            assertThatTrainingOfferMatchesEvent(actual, event);
        });
    }

    @Test
    void shouldOverwriteExistingTrainingOffer() {
        TrainingOfferPublishedEvent event = trainingOfferPublishedEvent();

        publish(event);
        publish(event);

        await().untilAsserted(() -> {
            assertThat(trainingOfferRepository.findAll())
                    .hasSize(1)
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, event));
        });
    }

    @Test
    void shouldCreateTrainingOffers() {
        TrainingOfferPublishedEvent trainingOfferPublishedEventOne = trainingOfferPublishedEvent();
        TrainingOfferPublishedEvent trainingOfferPublishedEventTwo = trainingOfferPublishedEvent();
        TrainingOfferPublishedEvent trainingOfferPublishedEventThree = trainingOfferPublishedEvent();
        TrainingOfferPublishedEvent trainingOfferPublishedEventFour = trainingOfferPublishedEvent();
        TrainingOfferPublishedEvent trainingOfferPublishedEventFive = trainingOfferPublishedEvent();

        publish(trainingOfferPublishedEventOne);
        publish(trainingOfferPublishedEventTwo);
        publish(trainingOfferPublishedEventThree);
        publish(trainingOfferPublishedEventFour);
        publish(trainingOfferPublishedEventFive);
        publish(trainingOfferPublishedEventTwo);
        publish(trainingOfferPublishedEventFive);

        await().untilAsserted(() -> {
            assertThat(trainingOfferRepository.findAll())
                    .hasSize(5)
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, trainingOfferPublishedEventOne))
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, trainingOfferPublishedEventTwo))
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, trainingOfferPublishedEventThree))
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, trainingOfferPublishedEventFour))
                    .anySatisfy(found -> assertThatTrainingOfferMatchesEvent(found, trainingOfferPublishedEventFive));
        });
    }

    private TrainingOfferPublishedEvent trainingOfferPublishedEvent() {
        TrainingOfferPublishedEvent trainingOfferPublishedEvent = randomTrainingOfferPublishedEvent();
        ids.add(trainingOfferPublishedEvent.trainingOfferId());

        return trainingOfferPublishedEvent;
    }

    private void publish(TrainingOfferPublishedEvent trainingOfferPublishedEvent) {
        kafkaTemplate.send("training-offer-published-event-topic", trainingOfferPublishedEvent);
    }

    private TrainingOffer trainingOfferFor(UUID trainingOfferId) {
        return trainingOfferRepository.findById(trainingOfferId).get();
    }

    private void assertThatTrainingOfferMatchesEvent(TrainingOffer actual, TrainingOfferPublishedEvent expected) {
        assertThatTrainingOffer(actual)
                .hasTrainingOfferId(expected.trainingOfferId())
                .hasTrainingOfferDraftId(expected.trainingOfferDraftId())
                .hasTrainingProgramId(expected.trainingProgramId())
                .hasTrainerId(expected.trainerId())
                .hasPriceAmount(expected.priceAmount())
                .hasPriceCurrency(expected.priceCurrencyCode())
                .hasAvailablePlaces(expected.maximumParticipants())
                .hasStartDate(expected.startDate())
                .hasStartTime(expected.startTime())
                .hasEndDate(expected.endDate())
                .hasEndTime(expected.endTime());
    }
}