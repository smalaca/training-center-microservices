package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.test.type.IntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
@Import(JpaOutboxMessageRepositoryFactory.class)
class JpaOutboxMessageRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private JpaOutboxMessageRepository repository;

    @Autowired
    private SpringOutboxMessageCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final List<UUID> messagesIds = new ArrayList<>();

    @AfterEach
    void deleteAllEvents() {
        if (!messagesIds.isEmpty()) {
            springRepository.deleteAllById(messagesIds);
        }
    }

    @Test
    void shouldPublishOfferRejected() {
        repository.publish(randomOfferRejected());
    }

    private OfferEvent randomOfferRejected() {
        return new OfferRejectedEvent(newEventId(), randomId(), "Dummy reason");
    }

    @Test
    void shouldPublishTrainingPurchased() {
        TrainingPurchasedEvent event = randomTrainingPurchasedEvent();

        publish(event);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderRejected() {
        OrderRejectedEvent event = randomOrderRejectedEvent();

        publish(event);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertOrderRejectedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderCancelled() {
        OrderCancelledEvent event = randomOrderCancelledEvent();

        publish(event);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertOrderCancelledEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderTerminatedEvent() {
        OrderTerminatedEvent event = randomOrderTerminatedEvent();

        publish(event);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOfferAcceptanceRequestedEvent() {
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        publish(event);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertOfferAcceptanceRequestedEventSaved(actual, event));
    }

    @Test
    void shouldPublishAcceptOfferCommand() {
        AcceptOfferCommand command = randomAcceptOfferCommand();

        publish(command);

        assertThat(springRepository.findAll()).anySatisfy(actual -> assertAcceptOfferCommand(actual, command));
    }

    @Test
    void shouldFindAllOutboxMessages() {
        TrainingPurchasedEvent eventOne = publish(randomTrainingPurchasedEvent());
        OrderRejectedEvent eventTwo = publish(randomOrderRejectedEvent());
        TrainingPurchasedEvent eventThree = publish(randomTrainingPurchasedEvent());
        TrainingPurchasedEvent eventFour = publish(randomTrainingPurchasedEvent());
        OrderCancelledEvent eventFive = publish(randomOrderCancelledEvent());
        OrderRejectedEvent eventSix = publish(randomOrderRejectedEvent());
        OrderTerminatedEvent eventSeven = publish(randomOrderTerminatedEvent());
        OrderTerminatedEvent eventEight = publish(randomOrderTerminatedEvent());
        OfferAcceptanceRequestedEvent eventNine = publish(randomOfferAcceptanceRequestedEvent());
        AcceptOfferCommand commandOne = publish(randomAcceptOfferCommand());

        assertThat(springRepository.findAll())
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventOne))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventTwo))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventThree))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventFour))
                .anySatisfy(actual -> assertOrderCancelledEventSaved(actual, eventFive))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventSix))
                .anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, eventSeven))
                .anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, eventEight))
                .anySatisfy(actual -> assertOfferAcceptanceRequestedEventSaved(actual, eventNine))
                .anySatisfy(actual -> assertAcceptOfferCommand(actual, commandOne));
    }

    private <T extends OrderEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }

    private OfferAcceptanceRequestedEvent publish(OfferAcceptanceRequestedEvent event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }

    private AcceptOfferCommand publish(AcceptOfferCommand command) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(command);
            messagesIds.add(command.commandId().commandId());
            return command;
        });
    }

    private AcceptOfferCommand randomAcceptOfferCommand() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new AcceptOfferCommand(commandId, randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().ean13());
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().ean13());
    }

    private OrderRejectedEvent randomOrderRejectedEvent() {
        return new OrderRejectedEvent(newEventId(), randomId(), FAKER.lorem().paragraph());
    }

    private TrainingPurchasedEvent randomTrainingPurchasedEvent() {
        return new TrainingPurchasedEvent(newEventId(), randomId(), randomId(), randomId(), randomId());
    }

    private OrderCancelledEvent randomOrderCancelledEvent() {
        return new OrderCancelledEvent(newEventId(), randomId(), randomId(), randomId(), randomId());
    }

    private OrderTerminatedEvent randomOrderTerminatedEvent() {
        return new OrderTerminatedEvent(newEventId(), randomId(), randomId(), randomId(), randomId());
    }

    private void assertAcceptOfferCommand(OutboxMessage actual, AcceptOfferCommand expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.commandId().commandId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.commandId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand");
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"firstName\" : \"" + expected.firstName())
                .contains("\"lastName\" : \"" + expected.lastName())
                .contains("\"email\" : \"" + expected.email());
    }

    private void assertOfferAcceptanceRequestedEventSaved(OutboxMessage actual, OfferAcceptanceRequestedEvent expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent");
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"discountCode\" : \"" + expected.discountCode())
                .contains("\"firstName\" : \"" + expected.firstName())
                .contains("\"lastName\" : \"" + expected.lastName())
                .contains("\"email\" : \"" + expected.email());
    }

    private void assertOrderRejectedEventSaved(OutboxMessage actual, OrderRejectedEvent expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"reason\" : \"" + expected.reason());
    }

    private void assertTrainingPurchasedEventSaved(OutboxMessage actual, TrainingPurchasedEvent expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
    }

    private void assertOrderCancelledEventSaved(OutboxMessage actual, OrderCancelledEvent expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
    }

    private void assertOrderTerminatedEventSaved(OutboxMessage actual, OrderTerminatedEvent expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getMessageType()).isEqualTo("com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
    }
}