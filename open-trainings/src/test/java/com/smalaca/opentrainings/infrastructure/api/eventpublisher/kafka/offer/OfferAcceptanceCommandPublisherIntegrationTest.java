package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.Optional;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import(OfferAcceptanceTestKafkaListener.class)
class OfferAcceptanceCommandPublisherIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private OfferAcceptanceCommandPublisher publisher;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private OfferAcceptanceTestKafkaListener consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishRegisterPersonCommand() {
        RegisterPersonCommand command = registerPersonCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.offeracceptancesaga.commands.RegisterPersonCommand> actual = consumer.registerPersonCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    private RegisterPersonCommand registerPersonCommand() {
        return RegisterPersonCommand.nextAfter(offerAcceptanceRequestedEvent());
    }

    private OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), randomDiscountCode());
    }

    private String randomDiscountCode() {
        return FAKER.code().ean13();
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.offeracceptancesaga.commands.RegisterPersonCommand actual, RegisterPersonCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.firstName()).isEqualTo(expected.firstName());
        assertThat(actual.lastName()).isEqualTo(expected.lastName());
        assertThat(actual.email()).isEqualTo(expected.email());
    }

    @Test
    void shouldPublishUseDiscountCodeCommand() {
        UseDiscountCodeCommand command = useDiscountCodeCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand> actual = consumer.useDiscountCodeCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    @Test
    void shouldPublishConfirmTrainingPriceCommand() {
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand> actual = consumer.confirmTrainingPriceCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    @Test
    void shouldPublishBookTrainingPlaceCommand() {
        BookTrainingPlaceCommand command = bookTrainingPlaceCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand> actual = consumer.bookTrainingPlaceCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    @Test
    void shouldPublishReturnDiscountCodeCommand() {
        ReturnDiscountCodeCommand command = returnDiscountCodeCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand> actual = consumer.returnDiscountCodeCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    private BookTrainingPlaceCommand bookTrainingPlaceCommand() {
        return BookTrainingPlaceCommand.nextAfter(randomTrainingPriceNotChangedEvent(), randomId(), randomId());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.commands.BookTrainingPlaceCommand actual, BookTrainingPlaceCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.participantId()).isEqualTo(expected.participantId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.trainingoffer.commands.ConfirmTrainingPriceCommand actual, ConfirmTrainingPriceCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.trainingOfferId()).isEqualTo(expected.trainingOfferId());
        assertThat(actual.priceAmount()).isEqualTo(expected.priceAmount());
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected.priceCurrencyCode());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.offeracceptancesaga.commands.UseDiscountCodeCommand actual, UseDiscountCodeCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.participantId()).isEqualTo(expected.participantId());
        assertThat(actual.trainingId()).isEqualTo(expected.trainingId());
        assertThat(actual.priceAmount()).isEqualTo(expected.priceAmount());
        assertThat(actual.priceCurrencyCode()).isEqualTo(expected.priceCurrencyCode());
        assertThat(actual.discountCode()).isEqualTo(expected.discountCode());
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommand() {
        return ConfirmTrainingPriceCommand.nextAfter(randomExpiredOfferAcceptanceRequestedEvent());
    }

    private ExpiredOfferAcceptanceRequestedEvent randomExpiredOfferAcceptanceRequestedEvent() {
        return new ExpiredOfferAcceptanceRequestedEvent(newEventId(), randomId(), randomId(), randomAmount(), randomCurrency());
    }

    private UseDiscountCodeCommand useDiscountCodeCommand() {
        return UseDiscountCodeCommand.nextAfter(randomTrainingPriceNotChangedEvent(), randomId(), randomId(), randomAmount(), randomCurrency(), randomDiscountCode());
    }

    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), randomId(), randomId());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.metadata.CommandId actual, CommandId expected) {
        assertThat(actual.commandId()).isEqualTo(expected.commandId());
        assertThat(actual.traceId()).isEqualTo(expected.traceId());
        assertThat(actual.correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.creationDateTime()).isEqualTo(expected.creationDateTime());
    }

    private ReturnDiscountCodeCommand returnDiscountCodeCommand() {
        return ReturnDiscountCodeCommand.nextAfter(randomTrainingPriceNotChangedEvent(), randomId(), randomDiscountCode());
    }

    private void assertThatContainsSameData(com.smalaca.schemaregistry.offeracceptancesaga.commands.ReturnDiscountCodeCommand actual, ReturnDiscountCodeCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.participantId()).isEqualTo(expected.participantId());
        assertThat(actual.discountCode()).isEqualTo(expected.discountCode());
    }
}
