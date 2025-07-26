package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand;
import com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand;
import com.smalaca.schemaregistry.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.NoAvailableTrainingPlacesLeftEventAssertion.assertThatNoAvailableTrainingPlacesLeftEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPlaceBookedEventAssertion.assertThatTrainingPlaceBookedEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceChangedEventAssertion.assertThatTrainingPriceChangedEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceNotChangedEventAssertion.assertThatTrainingPriceNotChangedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.trainingoffer.commands.confirm-training-price=" + TrainingOfferKafkaEventListenerIntegrationTest.CONFIRM_TRAINING_PRICE_COMMAND_TOPIC,
        "kafka.topics.trainingoffer.commands.book-training-place=" + TrainingOfferKafkaEventListenerIntegrationTest.BOOK_TRAINING_PLACE_COMMAND_TOPIC,
        "kafka.topics.trainingoffer.events.training-price-changed=" + TrainingOfferKafkaEventListenerIntegrationTest.TRAINING_PRICE_CHANGED_EVENT_TOPIC,
        "kafka.topics.trainingoffer.events.training-price-not-changed=" + TrainingOfferKafkaEventListenerIntegrationTest.TRAINING_PRICE_NOT_CHANGED_EVENT_TOPIC,
        "kafka.topics.trainingoffer.events.training-place-booked=" + TrainingOfferKafkaEventListenerIntegrationTest.TRAINING_PLACE_BOOKED_EVENT_TOPIC,
        "kafka.topics.trainingoffer.events.no-available-training-places-left=" + TrainingOfferKafkaEventListenerIntegrationTest.NO_AVAILABLE_TRAINING_PLACES_LEFT_EVENT_TOPIC
})
@Import(TrainingOfferPivotalEventTestConsumer.class)
class TrainingOfferKafkaEventListenerIntegrationTest {
    protected static final String CONFIRM_TRAINING_PRICE_COMMAND_TOPIC = "confirm-training-price-command-topic";
    protected static final String BOOK_TRAINING_PLACE_COMMAND_TOPIC = "book-training-place-command-topic";
    protected static final String TRAINING_PRICE_CHANGED_EVENT_TOPIC = "training-price-changed-event-topic";
    protected static final String TRAINING_PRICE_NOT_CHANGED_EVENT_TOPIC = "training-price-not-changed-event-topic";
    protected static final String TRAINING_PLACE_BOOKED_EVENT_TOPIC = "training-place-booked-event-topic";
    protected static final String NO_AVAILABLE_TRAINING_PLACES_LEFT_EVENT_TOPIC = "no-available-training-places-left-event-topic";

    private static final UUID TRAINING_OFFER_ID = UUID.randomUUID();
    private static final UUID TRAINING_OFFER_DRAFT_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();
    private static final UUID TRAINER_ID = UUID.randomUUID();
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE_AMOUNT = BigDecimal.valueOf(1000);
    private static final int MINIMUM_PARTICIPANTS = 5;
    private static final int MAXIMUM_PARTICIPANTS = 20;
    private static final LocalDate START_DATE = LocalDate.of(2023, 10, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 10, 5);
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(17, 0);
    private static final String NEW_CURRENCY = "EUR";
    private static final BigDecimal NEW_PRICE_AMOUNT = BigDecimal.valueOf(1500);

    @Autowired
    private KafkaTemplate<String, Object> producerFactory;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private TrainingOfferPivotalEventTestConsumer consumer;

    @Autowired
    private TrainingOfferRepository repository;

    private final TrainingOfferFactory factory = new TrainingOfferFactory();

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingPriceChangedEventWhenPriceAmountChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(NEW_PRICE_AMOUNT, CURRENCY);

        producerFactory.send(CONFIRM_TRAINING_PRICE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPriceChangedEvent> actual = consumer.trainingPriceChangedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPriceChangedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingOfferId(command.trainingOfferId())
                    .hasPriceAmount(PRICE_AMOUNT)
                    .hasPriceCurrency(CURRENCY);
        });
    }

    @Test
    void shouldPublishTrainingPriceChangedEventWhenPriceCurrencyChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(PRICE_AMOUNT, NEW_CURRENCY);

        producerFactory.send(CONFIRM_TRAINING_PRICE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPriceChangedEvent> actual = consumer.trainingPriceChangedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPriceChangedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingOfferId(command.trainingOfferId())
                    .hasPriceAmount(PRICE_AMOUNT)
                    .hasPriceCurrency(CURRENCY);
        });
    }

    @Test
    void shouldPublishTrainingPriceNotChangedEventWhenPriceNotChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(PRICE_AMOUNT, CURRENCY);

        producerFactory.send(CONFIRM_TRAINING_PRICE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPriceNotChangedEvent> actual = consumer.trainingPriceNotChangedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPriceNotChangedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingOfferId(command.trainingOfferId());
        });
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommand(BigDecimal amount, String currency) {
        return new ConfirmTrainingPriceCommand(commandId(), UUID.randomUUID(), TRAINING_OFFER_ID, amount, currency);
    }

    @Test
    void shouldPublishTrainingPlaceBookedEventWhenPlacesAvailable() {
        existingTrainingOffer();
        BookTrainingPlaceCommand command = bookTrainingPlaceCommand();

        producerFactory.send(BOOK_TRAINING_PLACE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPlaceBookedEvent> actual = consumer.trainingPlaceBookedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPlaceBookedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingOfferId(command.trainingOfferId())
                    .hasParticipantId(command.participantId());
        });
    }

    @Test
    void shouldPublishNoAvailableTrainingPlacesLeftEventWhenNoPlacesAvailable() {
        existingTrainingOfferWithAllBookedPlaces();
        BookTrainingPlaceCommand command = bookTrainingPlaceCommand();

        producerFactory.send(BOOK_TRAINING_PLACE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<NoAvailableTrainingPlacesLeftEvent> actual = consumer.noAvailableTrainingPlacesLeftEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatNoAvailableTrainingPlacesLeftEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingOfferId(command.trainingOfferId())
                    .hasParticipantId(command.participantId());
        });
    }

    private BookTrainingPlaceCommand bookTrainingPlaceCommand() {
        return new BookTrainingPlaceCommand(commandId(), UUID.randomUUID(), UUID.randomUUID(), TRAINING_OFFER_ID);
    }

    private CommandId commandId() {
        return new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), now());
    }

    private void existingTrainingOfferWithAllBookedPlaces() {
        TrainingOffer trainingOffer = trainingOffer();
        IntStream.range(0, MAXIMUM_PARTICIPANTS).forEach(i -> trainingOffer.book(internalBookTrainingPlaceCommand()));
        repository.save(trainingOffer);
    }

    private com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand internalBookTrainingPlaceCommand() {
        com.smalaca.trainingoffer.domain.commandid.CommandId commandId = new com.smalaca.trainingoffer.domain.commandid.CommandId(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), now());
        return new com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand(commandId, UUID.randomUUID(), UUID.randomUUID(), TRAINING_OFFER_ID);
    }

    private void existingTrainingOffer() {
        repository.save(trainingOffer());
    }

    private TrainingOffer trainingOffer() {
        TrainingOfferPublishedEvent event = TrainingOfferPublishedEvent.create(
                TRAINING_OFFER_ID, TRAINING_OFFER_DRAFT_ID, TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);

        return factory.create(event);
    }
}