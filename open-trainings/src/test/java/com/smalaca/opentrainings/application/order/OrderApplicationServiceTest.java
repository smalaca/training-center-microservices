package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.order.GivenOrder;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderAssertion;
import com.smalaca.opentrainings.domain.order.OrderInFinalStateException;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTerminationNotYetPermittedException;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEventAssertion;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEventAssertion;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEventAssertion;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import com.smalaca.opentrainings.domain.paymentmethod.UnsupportedPaymentMethodException;
import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.order.events.OrderCancelledEventAssertion.assertThatOrderCancelledEvent;
import static com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion.assertThatOrderRejectedEvent;
import static com.smalaca.opentrainings.domain.order.events.OrderTerminatedEventAssertion.assertThatOrderTerminatedEvent;
import static com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEventAssertion.assertThatTrainingPurchasedEvent;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.failed;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.successful;
import static com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod.CREDIT_CARD;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceTest {
    private static final Faker FAKER = new Faker();
    private static final UUID ORDER_ID = randomId();
    private static final UUID OFFER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final Price TRAINING_PRICE = randomPrice();
    private static final Price FINAL_PRICE = randomPrice();
    private static final String DISCOUNT_CODE = FAKER.code().toString();

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final Clock clock = mock(Clock.class);
    private final OrderApplicationService service = new OrderApplicationServiceFactory().orderApplicationService(orderRepository, eventRegistry, paymentGateway, clock);
    private final GivenOrderFactory given = GivenOrderFactory.create(orderRepository);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(now());
    }

    @Test
    void shouldInitiateOrderWithDiscountCodeUsed() {
        LocalDateTime creationDateTime = LocalDateTime.of(LocalDate.of(2024, 11, 1), LocalTime.now());
        given(clock.now()).willReturn(creationDateTime);
        AcceptOfferCommand command = acceptOfferCommand().withDiscountCodeUsed(DISCOUNT_CODE, FINAL_PRICE).build();
        OfferAcceptedEvent event = OfferAcceptedEvent.nextAfter(command, TRAINING_ID, TRAINING_PRICE);

        service.initiate(event);

        thenOrderSaved()
                .isInitiated()
                .hasOrderNumberStartingWith("ORD/2024/11/" + PARTICIPANT_ID + "/")
                .hasOfferId(OFFER_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingId(TRAINING_ID)
                .hasCreationDateTime(creationDateTime)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(FINAL_PRICE)
                .hasDiscountCodeUsed(DISCOUNT_CODE);
    }

    @Test
    void shouldInitiateOrderWithDiscountCodeAlreadyUsed() {
        LocalDateTime creationDateTime = LocalDateTime.of(LocalDate.of(2024, 11, 1), LocalTime.now());
        given(clock.now()).willReturn(creationDateTime);
        AcceptOfferCommand command = acceptOfferCommand().withDiscountCodeAlreadyUsed(DISCOUNT_CODE).build();
        OfferAcceptedEvent event = OfferAcceptedEvent.nextAfter(command, TRAINING_ID, TRAINING_PRICE);

        service.initiate(event);

        thenOrderSaved()
                .isInitiated()
                .hasOrderNumberStartingWith("ORD/2024/11/" + PARTICIPANT_ID + "/")
                .hasOfferId(OFFER_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingId(TRAINING_ID)
                .hasCreationDateTime(creationDateTime)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasNoFinalPrice()
                .hasDiscountCodeAlreadyUsed(DISCOUNT_CODE);
    }

    @Test
    void shouldInitiateOrderWithoutDiscountCode() {
        LocalDateTime creationDateTime = LocalDateTime.of(LocalDate.of(2011, 9, 1), LocalTime.now());
        given(clock.now()).willReturn(creationDateTime);
        OfferAcceptedEvent event = OfferAcceptedEvent.nextAfter(acceptOfferCommand().build(), TRAINING_ID, TRAINING_PRICE);

        service.initiate(event);

        thenOrderSaved()
                .isInitiated()
                .hasOrderNumberStartingWith("ORD/2011/09/" + PARTICIPANT_ID + "/")
                .hasOfferId(OFFER_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingId(TRAINING_ID)
                .hasCreationDateTime(creationDateTime)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasNoFinalPrice()
                .hasNoDiscountCode();
    }

    private AcceptOfferCommand.Builder acceptOfferCommand() {
        OfferAcceptanceSagaEvent event = new TrainingPriceNotChangedEvent(newEventId(), OFFER_ID, TRAINING_ID);

        return acceptOfferCommandBuilder(event, PARTICIPANT_ID);
    }

    @Test
    void shouldInterruptOrderConfirmationIfOrderAlreadyConfirmed() {
        givenOrder().confirmed();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.confirm(confirmOrderCommand()));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CONFIRMED");
    }

    @Test
    void shouldInterruptOrderConfirmationIfOrderAlreadyRejected() {
        givenOrder().rejected();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.confirm(confirmOrderCommand()));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptOrderConfirmationIfOrderAlreadyTerminated() {
        givenOrder().createdMinutesAgo(20).terminated();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.confirm(confirmOrderCommand()));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already TERMINATED");
    }

    @Test
    void shouldInterruptOrderConfirmationIfOrderAlreadyCancelled() {
        givenOrder().cancelled();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.confirm(confirmOrderCommand()));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CANCELLED");
    }

    @Test
    void shouldInterruptOrderConfirmationIfUnsupportedPaymentMethodGiven() {
        givenOrder().initiated();
        ConfirmOrderCommand command = new ConfirmOrderCommand(ORDER_ID, "UNSUPPORTED");

        UnsupportedPaymentMethodException actual = assertThrows(UnsupportedPaymentMethodException.class, () -> service.confirm(command));

        assertThat(actual).hasMessage("Unsupported payment method: UNSUPPORTED");
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOrderWhenOlderThanTenMinutes(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.confirm(confirmOrderCommand());

        thenOrderSaved().isRejected();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldPublishOrderRejectedWhenOlderThanTenMinutes(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.confirm(confirmOrderCommand());

        thenOrderRejectedEventPublished()
                .hasOrderId(ORDER_ID)
                .hasReason("Order expired");
    }

    @Test
    void shouldRejectOrderWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder().initiated();

        service.confirm(confirmOrderCommand());

        thenOrderSaved().isRejected();
    }

    @Test
    void shouldPublishOrderRejectedWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder().initiated();

        service.confirm(confirmOrderCommand());

        thenOrderRejectedEventPublished()
                .hasOrderId(ORDER_ID)
                .hasReason("Could not complete payment.");
    }

    private OrderRejectedEventAssertion thenOrderRejectedEventPublished() {
        ArgumentCaptor<OrderRejectedEvent> captor = ArgumentCaptor.forClass(OrderRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OrderRejectedEvent actual = captor.getValue();

        return assertThatOrderRejectedEvent(actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldConfirmOrder(int minutes) {
        givenPayment(successful());
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.confirm(confirmOrderCommand());

        thenOrderSaved().isConfirmed();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldPublishTrainingPurchasedWhenOrderConfirmed(int minutes) {
        givenPayment(successful());
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.confirm(confirmOrderCommand());

        thenTrainingPurchasedEventPublished()
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private ConfirmOrderCommand confirmOrderCommand() {
        return new ConfirmOrderCommand(ORDER_ID, "CREDIT_CARD");
    }

    private TrainingPurchasedEventAssertion thenTrainingPurchasedEventPublished() {
        ArgumentCaptor<TrainingPurchasedEvent> captor = ArgumentCaptor.forClass(TrainingPurchasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        TrainingPurchasedEvent actual = captor.getValue();

        return assertThatTrainingPurchasedEvent(actual);
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyConfirmed() {
        givenOrder().confirmed();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CONFIRMED");
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyRejected() {
        givenOrder().rejected();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyTerminated() {
        givenOrder().createdMinutesAgo(20).terminated();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already TERMINATED");
    }

    @Test
    void shouldCancelOrder() {
        givenOrder().initiated();

        service.cancel(ORDER_ID);

        thenOrderSaved().isCancelled();
    }

    private void givenPayment(PaymentResponse paymentResponse) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(ORDER_ID)
                .participantId(PARTICIPANT_ID)
                .price(FINAL_PRICE)
                .paymentMethod(CREDIT_CARD)
                .build();
        given(paymentGateway.pay(paymentRequest)).willReturn(paymentResponse);
    }

    @Test
    void shouldPublishOrderCancelledEventWhenOrderCancelled() {
        givenOrder().initiated();

        service.cancel(ORDER_ID);

        thenOrderCancelledEventPublished()
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderCancelledEventAssertion thenOrderCancelledEventPublished() {
        ArgumentCaptor<OrderCancelledEvent> captor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OrderCancelledEvent actual = captor.getValue();

        return assertThatOrderCancelledEvent(actual);
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyConfirmed() {
        givenOrder().confirmed();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CONFIRMED");
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyRejected() {
        givenOrder().rejected();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyCancelled() {
        givenOrder().cancelled();

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CANCELLED");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldInterruptOrderTerminationIfOrderTooNew(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated();

        OrderTerminationNotYetPermittedException actual = assertThrows(OrderTerminationNotYetPermittedException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order with id " + ORDER_ID + " cannot be terminated yet.");
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 13, 20, 100})
    void shouldTerminateOrder(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.terminate(ORDER_ID);

        thenOrderSaved().isTerminated();
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 13, 20, 100})
    void shouldPublishOrderTerminatedEventWhenOrderTerminated(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated();

        service.terminate(ORDER_ID);

        thenOrderTerminatedEventPublished()
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderTerminatedEventAssertion thenOrderTerminatedEventPublished() {
        ArgumentCaptor<OrderTerminatedEvent> captor = ArgumentCaptor.forClass(OrderTerminatedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OrderTerminatedEvent actual = captor.getValue();

        return assertThatOrderTerminatedEvent(actual);
    }

    private OrderAssertion thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());
        Order actual = captor.getValue();

        return assertThatOrder(actual);
    }

    private GivenOrder givenOrder() {
        return given
                .order(ORDER_ID)
                .offerId(OFFER_ID)
                .trainingId(TRAINING_ID)
                .participantId(PARTICIPANT_ID)
                .trainingPrice(TRAINING_PRICE)
                .finalPrice(FINAL_PRICE)
                .discountCode(DISCOUNT_CODE);
    }
}