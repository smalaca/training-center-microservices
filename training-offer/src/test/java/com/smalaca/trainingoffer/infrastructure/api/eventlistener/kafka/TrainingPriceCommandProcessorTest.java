package com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka;

import com.smalaca.schemaregistry.metadata.CommandId;
import com.smalaca.schemaregistry.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceChangedEventAssertion.assertThatTrainingPriceChangedEvent;
import static com.smalaca.trainingoffer.infrastructure.api.eventlistener.kafka.TrainingPriceNotChangedEventAssertion.assertThatTrainingPriceNotChangedEvent;
import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingPriceCommandProcessorTest {
    private static final Faker FAKER = new Faker();
    private static final String TRAINING_PRICE_CHANGED_TOPIC = "training-price-changed-topic";
    private static final String TRAINING_PRICE_NOT_CHANGED_TOPIC = "training-price-not-changed-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final TrainingPriceCommandProcessor processor = new TrainingPriceCommandProcessor(
            kafkaTemplate, TRAINING_PRICE_CHANGED_TOPIC, TRAINING_PRICE_NOT_CHANGED_TOPIC);

    @ParameterizedTest
    @ValueSource(strings = {
            "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
            "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
            "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33"})
    void shouldPublishTrainingPriceChangedEventWhenTrainingIdIsInChangedPriceList(String trainingId) {
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommandWithChangedPrice(trainingId);

        processor.process(command);

        TrainingPriceChangedEvent event = thenTrainingPriceChangedEventPublished();
        assertThatTrainingPriceChangedEvent(event)
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasTrainingId(command.trainingId())
                .hasPriceAmountDifferentThan(command.priceAmount())
                .hasPriceCurrencyCode(command.priceCurrencyCode());
    }

    private TrainingPriceChangedEvent thenTrainingPriceChangedEventPublished() {
        ArgumentCaptor<TrainingPriceChangedEvent> captor = ArgumentCaptor.forClass(TrainingPriceChangedEvent.class);
        then(kafkaTemplate).should().send(eq(TRAINING_PRICE_CHANGED_TOPIC), captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldPublishTrainingPriceNotChangedEventWhenTrainingIdIsNotInChangedPriceList() {
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommandWithNotChangedPrice();

        processor.process(command);

        TrainingPriceNotChangedEvent event = thenTrainingPriceNotChangedEventPublished();
        assertThatTrainingPriceNotChangedEvent(event)
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasTrainingId(command.trainingId());
    }

    private TrainingPriceNotChangedEvent thenTrainingPriceNotChangedEventPublished() {
        ArgumentCaptor<TrainingPriceNotChangedEvent> captor = ArgumentCaptor.forClass(TrainingPriceNotChangedEvent.class);
        then(kafkaTemplate).should().send(eq(TRAINING_PRICE_NOT_CHANGED_TOPIC), captor.capture());
        return captor.getValue();
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommandWithChangedPrice(String trainingId) {
        return new ConfirmTrainingPriceCommand(commandId(), id(), UUID.fromString(trainingId), priceAmount(), currencyCode());
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