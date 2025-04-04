package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
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

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;
import static com.smalaca.opentrainings.infrastructure.outbox.jpa.OutboxMessageAssertion.assertThatOutboxMessage;
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
    void shouldFindAllOutboxMessages() {
        ExpiredOfferAcceptanceRequestedEvent expiredOfferAcceptanceRequestedEvent = publish(randomExpiredOfferAcceptanceRequestedEvent());
        NotAvailableOfferAcceptanceRequestedEvent notAvailableOfferAcceptanceRequestedEvent = publish(randomNotAvailableOfferAcceptanceRequestedEvent());
        OfferAcceptedEvent offerAcceptedEvent = publish(randomOfferAcceptedEvent());
        OfferRejectedEvent offerRejectedEvent = publish(randomOfferRejectedEvent());
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = publish(randomUnexpiredOfferAcceptanceRequestedEvent());
        OrderCancelledEvent orderCancelledEvent = publish(randomOrderCancelledEvent());
        OrderRejectedEvent orderRejectedEvent = publish(randomOrderRejectedEvent());
        OrderTerminatedEvent orderTerminatedEvent = publish(randomOrderTerminatedEvent());
        TrainingPurchasedEvent trainingPurchasedEvent = publish(randomTrainingPurchasedEvent());
        AlreadyRegisteredPersonFoundEvent alreadyRegisteredPersonFoundEvent = publish(randomAlreadyRegisteredPersonFoundEvent());
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = publish(randomOfferAcceptanceRequestedEvent());
        PersonRegisteredEvent personRegisteredEvent = publish(randomPersonRegisteredEvent());
        AcceptOfferCommand acceptOfferCommand = publish(randomAcceptOfferCommand());
        BeginOfferAcceptanceCommand beginOfferAcceptanceCommand = publish(randomBeginOfferAcceptanceCommand());
        RegisterPersonCommand registerPersonCommand = publish(randomRegisterPersonCommand());
        RejectOfferCommand rejectOfferCommand = publish(randomRejectOfferCommand());
        TrainingPriceChangedEvent trainingPriceChangedEvent = publish(randomTrainingPriceChangedEvent());
        TrainingPriceNotChangedEvent trainingPriceNotChangedEvent = publish(randomTrainingPriceNotChangedEvent());
        ConfirmTrainingPriceCommand confirmTrainingPriceCommand = publish(randomConfirmTrainingPriceCommand());
        UseDiscountCodeCommand useDiscountCodeCommand = publish(randomUseDiscountCodeCommand());
        DiscountCodeUsedEvent discountCodeUsedEvent = publish(randomDiscountCodeUsedEvent());
        DiscountCodeAlreadyUsedEvent discountCodeAlreadyUsedEvent = publish(randomDiscountCodeAlreadyUsedEvent());
        TrainingPlaceBookedEvent trainingPlaceBookedEvent = publish(randomTrainingPlaceBookedEvent());
        NoAvailableTrainingPlacesLeftEvent noAvailableTrainingPlacesLeftEvent = publish(randomNoAvailableTrainingPlacesLeftEvent());
        BookTrainingPlaceCommand bookTrainingPlaceCommand = publish(randomBookTrainingPlaceCommand());
        ReturnDiscountCodeCommand returnDiscountCodeCommand = publish(randomReturnDiscountCodeCommand());
        DiscountCodeReturnedEvent discountCodeReturnedEvent = publish(randomDiscountCodeReturnedEvent());

        assertThat(springRepository.findAll())
                .anySatisfy(actual -> assertExpiredOfferAcceptanceRequestedEventSaved(actual, expiredOfferAcceptanceRequestedEvent))
                .anySatisfy(actual -> assertNotAvailableOfferAcceptanceRequestedEvent(actual, notAvailableOfferAcceptanceRequestedEvent))
                .anySatisfy(actual -> assertUnexpiredOfferAcceptanceRequestedEvent(actual, unexpiredOfferAcceptanceRequestedEvent))
                .anySatisfy(actual -> assertAlreadyRegisteredPersonFoundEvent(actual, alreadyRegisteredPersonFoundEvent))
                .anySatisfy(actual -> assertPersonRegisteredEvent(actual, personRegisteredEvent))
                .anySatisfy(actual -> assertBeginOfferAcceptanceCommand(actual, beginOfferAcceptanceCommand))
                .anySatisfy(actual -> assertRegisterPersonCommand(actual, registerPersonCommand))
                .anySatisfy(actual -> assertRejectOfferCommand(actual, rejectOfferCommand))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, trainingPurchasedEvent))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, orderRejectedEvent))
                .anySatisfy(actual -> assertOrderCancelledEventSaved(actual, orderCancelledEvent))
                .anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, orderTerminatedEvent))
                .anySatisfy(actual -> assertOfferAcceptanceRequestedEventSaved(actual, offerAcceptanceRequestedEvent))
                .anySatisfy(actual -> assertOfferAcceptedEventSaved(actual, offerAcceptedEvent))
                .anySatisfy(actual -> assertOfferRejectedEventSaved(actual, offerRejectedEvent))
                .anySatisfy(actual -> assertAcceptOfferCommand(actual, acceptOfferCommand))
                .anySatisfy(actual -> assertTrainingPriceChangedEvent(actual, trainingPriceChangedEvent))
                .anySatisfy(actual -> assertTrainingPriceNotChangedEvent(actual, trainingPriceNotChangedEvent))
                .anySatisfy(actual -> assertConfirmTrainingPriceCommand(actual, confirmTrainingPriceCommand))
                .anySatisfy(actual -> assertUseDiscountCodeCommand(actual, useDiscountCodeCommand))
                .anySatisfy(actual -> assertDiscountCodeUsedEvent(actual, discountCodeUsedEvent))
                .anySatisfy(actual -> assertDiscountCodeAlreadyUsedEvent(actual, discountCodeAlreadyUsedEvent))
                .anySatisfy(actual -> assertTrainingPlaceBookedEvent(actual, trainingPlaceBookedEvent))
                .anySatisfy(actual -> assertNoAvailableTrainingPlacesLeftEvent(actual, noAvailableTrainingPlacesLeftEvent))
                .anySatisfy(actual -> assertBookTrainingPlaceCommand(actual, bookTrainingPlaceCommand))
                .anySatisfy(actual -> assertReturnDiscountCodeCommand(actual, returnDiscountCodeCommand))
                .anySatisfy(actual -> assertDiscountCodeReturnedEvent(actual, discountCodeReturnedEvent));
    }

    private <T extends OrderEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }

    private <T extends OfferAcceptanceSagaEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }

    private <T extends OfferAcceptanceSagaCommand> T publish(T command) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(command);
            messagesIds.add(command.commandId().commandId());
            return command;
        });
    }

    private ReturnDiscountCodeCommand randomReturnDiscountCodeCommand() {
        return ReturnDiscountCodeCommand.nextAfter(randomDiscountCodeUsedEvent(), randomId(), randomDiscountCode());
    }

    private NoAvailableTrainingPlacesLeftEvent randomNoAvailableTrainingPlacesLeftEvent() {
        return new NoAvailableTrainingPlacesLeftEvent(newEventId(), randomId(), randomId(), randomId());
    }

    private TrainingPlaceBookedEvent randomTrainingPlaceBookedEvent() {
        return new TrainingPlaceBookedEvent(newEventId(), randomId(), randomId(), randomId());
    }

    private DiscountCodeAlreadyUsedEvent randomDiscountCodeAlreadyUsedEvent() {
        return new DiscountCodeAlreadyUsedEvent(newEventId(), randomId(), randomId(), randomId(), randomDiscountCode());
    }

    private DiscountCodeUsedEvent randomDiscountCodeUsedEvent() {
        return new DiscountCodeUsedEvent(newEventId(), randomId(), randomId(), randomId(), randomDiscountCode(), randomAmount(), randomAmount(), randomCurrency());
    }

    private DiscountCodeReturnedEvent randomDiscountCodeReturnedEvent() {
        return new DiscountCodeReturnedEvent(newEventId(), randomId(), randomId(), randomDiscountCode());
    }

    private UseDiscountCodeCommand randomUseDiscountCodeCommand() {
        return UseDiscountCodeCommand.nextAfter(randomTrainingPriceNotChangedEvent(), randomId(), randomId(), randomAmount(), randomCurrency(), randomDiscountCode());
    }

    private OfferAcceptedEvent randomOfferAcceptedEvent() {
        return OfferAcceptedEvent.nextAfter(randomAcceptOfferCommand(), randomId(), randomPrice());
    }

    private String randomDiscountCode() {
        return FAKER.code().ean13();
    }

    private BookTrainingPlaceCommand randomBookTrainingPlaceCommand() {
        return BookTrainingPlaceCommand.nextAfter(randomTrainingPriceNotChangedEvent(), randomId(), randomId());
    }

    private ConfirmTrainingPriceCommand randomConfirmTrainingPriceCommand() {
        return ConfirmTrainingPriceCommand.nextAfter(randomExpiredOfferAcceptanceRequestedEvent());
    }

    private NotAvailableOfferAcceptanceRequestedEvent randomNotAvailableOfferAcceptanceRequestedEvent() {
        return NotAvailableOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), FAKER.lorem().paragraph());
    }

    private UnexpiredOfferAcceptanceRequestedEvent randomUnexpiredOfferAcceptanceRequestedEvent() {
        return UnexpiredOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), randomId(), randomPrice());
    }

    private AlreadyRegisteredPersonFoundEvent randomAlreadyRegisteredPersonFoundEvent() {
        return new AlreadyRegisteredPersonFoundEvent(newEventId(), randomId(), randomId());
    }

    private RegisterPersonCommand randomRegisterPersonCommand() {
        return RegisterPersonCommand.nextAfter(randomOfferAcceptanceRequestedEvent());
    }

    private OfferRejectedEvent randomOfferRejectedEvent() {
        return OfferRejectedEvent.nextAfter(randomRejectOfferCommand());
    }

    private RejectOfferCommand randomRejectOfferCommand() {
        return RejectOfferCommand.nextAfter(randomTrainingPriceChangedEvent());
    }

    private TrainingPriceChangedEvent randomTrainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(newEventId(), randomId(), randomId(), randomAmount(), randomCurrency());
    }

    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), randomId(), randomId());
    }

    private ExpiredOfferAcceptanceRequestedEvent randomExpiredOfferAcceptanceRequestedEvent() {
        return ExpiredOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), randomId(), randomPrice());
    }

    private BeginOfferAcceptanceCommand randomBeginOfferAcceptanceCommand() {
        return BeginOfferAcceptanceCommand.nextAfter(randomOfferAcceptanceRequestedEvent());
    }

    private AcceptOfferCommand randomAcceptOfferCommand() {
        OfferAcceptanceSagaEvent event = randomTrainingPriceNotChangedEvent();

        return acceptOfferCommandBuilder(event, randomId())
                .withDiscountCodeUsed(randomDiscountCode())
                .withFinalPrice(randomPrice())
                .build();
    }

    private PersonRegisteredEvent randomPersonRegisteredEvent() {
        return new PersonRegisteredEvent(newEventId(), randomId(), randomId());
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), randomDiscountCode());
    }

    private OrderRejectedEvent randomOrderRejectedEvent() {
        return OrderRejectedEvent.expired(randomId());
    }

    private TrainingPurchasedEvent randomTrainingPurchasedEvent() {
        return TrainingPurchasedEvent.create(randomId(), randomId(), randomId(), randomId());
    }

    private OrderCancelledEvent randomOrderCancelledEvent() {
        return OrderCancelledEvent.create(randomId(), randomId(), randomId(), randomId());
    }

    private OrderTerminatedEvent randomOrderTerminatedEvent() {
        return OrderTerminatedEvent.create(randomId(), randomId(), randomId(), randomId());
    }

    private void assertOfferAcceptedEventSaved(OutboxMessage actual, OfferAcceptedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);

    }

    private void assertOfferRejectedEventSaved(OutboxMessage actual, OfferRejectedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertAcceptOfferCommand(OutboxMessage actual, AcceptOfferCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertOfferAcceptanceRequestedEventSaved(OutboxMessage actual, OfferAcceptanceRequestedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertOrderRejectedEventSaved(OutboxMessage actual, OrderRejectedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertTrainingPurchasedEventSaved(OutboxMessage actual, TrainingPurchasedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertOrderCancelledEventSaved(OutboxMessage actual, OrderCancelledEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertOrderTerminatedEventSaved(OutboxMessage actual, OrderTerminatedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertExpiredOfferAcceptanceRequestedEventSaved(OutboxMessage actual, ExpiredOfferAcceptanceRequestedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertNotAvailableOfferAcceptanceRequestedEvent(OutboxMessage actual, NotAvailableOfferAcceptanceRequestedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertUnexpiredOfferAcceptanceRequestedEvent(OutboxMessage actual, UnexpiredOfferAcceptanceRequestedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertAlreadyRegisteredPersonFoundEvent(OutboxMessage actual, AlreadyRegisteredPersonFoundEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertPersonRegisteredEvent(OutboxMessage actual, PersonRegisteredEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertBeginOfferAcceptanceCommand(OutboxMessage actual, BeginOfferAcceptanceCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertRegisterPersonCommand(OutboxMessage actual, RegisterPersonCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertRejectOfferCommand(OutboxMessage actual, RejectOfferCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertTrainingPriceChangedEvent(OutboxMessage actual, TrainingPriceChangedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertTrainingPriceNotChangedEvent(OutboxMessage actual, TrainingPriceNotChangedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertConfirmTrainingPriceCommand(OutboxMessage actual, ConfirmTrainingPriceCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertUseDiscountCodeCommand(OutboxMessage actual, UseDiscountCodeCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertDiscountCodeUsedEvent(OutboxMessage actual, DiscountCodeUsedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertDiscountCodeAlreadyUsedEvent(OutboxMessage actual, DiscountCodeAlreadyUsedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertTrainingPlaceBookedEvent(OutboxMessage actual, TrainingPlaceBookedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertNoAvailableTrainingPlacesLeftEvent(OutboxMessage actual, NoAvailableTrainingPlacesLeftEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertBookTrainingPlaceCommand(OutboxMessage actual, BookTrainingPlaceCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertReturnDiscountCodeCommand(OutboxMessage actual, ReturnDiscountCodeCommand expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.commandId().commandId())
                .hasOccurredOn(expected.commandId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ReturnDiscountCodeCommand")
                .hasPayloadThatContainsAllDataFrom(expected);
    }

    private void assertDiscountCodeReturnedEvent(OutboxMessage actual, DiscountCodeReturnedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }
}