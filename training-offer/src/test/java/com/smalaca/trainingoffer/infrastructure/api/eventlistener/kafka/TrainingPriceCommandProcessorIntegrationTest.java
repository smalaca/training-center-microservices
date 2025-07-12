package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;
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
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceChangedEventAssertion.assertThatTrainingPriceChangedEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceNotChangedEventAssertion.assertThatTrainingPriceNotChangedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.command.confirm-training-price=" + TrainingPriceCommandProcessorIntegrationTest.CONFIRM_TRAINING_PRICE_COMMAND_TOPIC,
        "kafka.topics.event.training-price-changed=" + TrainingPriceCommandProcessorIntegrationTest.TRAINING_PRICE_CHANGED_EVENT_TOPIC,
        "kafka.topics.event.training-price-not-changed=" + TrainingPriceCommandProcessorIntegrationTest.TRAINING_PRICE_NOT_CHANGED_EVENT_TOPIC
})
@Import(TrainingOfferPivotalEventTestConsumer.class)
class TrainingPriceCommandProcessorIntegrationTest {
    private static final Faker FAKER = new Faker();
    protected static final String CONFIRM_TRAINING_PRICE_COMMAND_TOPIC = "confirm-training-price-command-topic";
    protected static final String TRAINING_PRICE_CHANGED_EVENT_TOPIC = "training-price-changed-event-topic";
    protected static final String TRAINING_PRICE_NOT_CHANGED_EVENT_TOPIC = "training-price-not-changed-event-topic";

    @Autowired
    private KafkaTemplate<String, Object> producerFactory;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private TrainingOfferPivotalEventTestConsumer consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishTrainingPriceChangedEventWhenTrainingIdIsInChangedPriceList() {
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommandWithChangedPrice();

        producerFactory.send(CONFIRM_TRAINING_PRICE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPriceChangedEvent> actual = consumer.trainingPriceChangedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPriceChangedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingId(command.trainingId())
                    .hasPriceAmountDifferentThan(command.priceAmount())
                    .hasPriceCurrencyCode(command.priceCurrencyCode());
        });
    }

    @Test
    void shouldPublishTrainingPriceNotChangedEventWhenTrainingIdIsNotInChangedPriceList() {
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommandWithNotChangedPrice();

        producerFactory.send(CONFIRM_TRAINING_PRICE_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<TrainingPriceNotChangedEvent> actual = consumer.trainingPriceNotChangedEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatTrainingPriceNotChangedEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasTrainingId(command.trainingId());
        });
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommandWithChangedPrice() {
        UUID trainingId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
        return new ConfirmTrainingPriceCommand(commandId(), id(), trainingId, priceAmount(), currencyCode());
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommandWithNotChangedPrice() {
        return new ConfirmTrainingPriceCommand(commandId(), id(), id(), priceAmount(), currencyCode());
    }

    private String currencyCode() {
        return FAKER.currency().code();
    }

    private BigDecimal priceAmount() {
        return BigDecimal.valueOf(RandomUtils.nextInt(1000, 10000));
    }

    private CommandId commandId() {
        return new CommandId(id(), id(), id(), now());
    }

    private static UUID id() {
        return UUID.randomUUID();
    }
}
