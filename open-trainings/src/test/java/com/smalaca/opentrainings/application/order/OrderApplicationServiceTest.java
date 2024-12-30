package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.GivenOrder;
import com.smalaca.opentrainings.domain.order.GivenOrderFactory;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderInFinalStateException;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTerminationNotYetPermittedException;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.order.events.OrderCancelledEventAssertion.assertThatOrderCancelledEvent;
import static com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion.assertThatOrderRejectedEvent;
import static com.smalaca.opentrainings.domain.order.events.OrderTerminatedEventAssertion.assertThatOrderTerminatedEvent;
import static com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEventAssertion.assertThatTrainingPurchasedEvent;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.failed;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.successful;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceTest {
    private static final String CURRENCY = randomCurrency();
    private static final BigDecimal AMOUNT = randomAmount();
    private static final UUID ORDER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final Clock clock = mock(Clock.class);
    private final OrderApplicationService service = new OrderApplicationService(orderRepository, eventRegistry, paymentGateway, clock);
    private final GivenOrderFactory given = GivenOrderFactory.create(orderRepository);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(now());
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOrderWhenOlderThanTenMinutes(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldPublishOrderRejectedWhenOlderThanTenMinutes(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
        assertThatOrderRejectedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasReason("Order expired");
    }

    @Test
    void shouldRejectOrderWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder().initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @Test
    void shouldPublishOrderRejectedWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder().initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
        assertThatOrderRejectedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasReason("Could not complete payment.");
    }

    private OrderRejectedEvent thenOrderRejectedEventPublished() {
        ArgumentCaptor<OrderRejectedEvent> captor = ArgumentCaptor.forClass(OrderRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldConfirmOrder(int minutes) {
        givenPayment(successful());
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isConfirmed();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldPublishTrainingPurchasedWhenOrderConfirmed(int minutes) {
        givenPayment(successful());
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.confirm(ORDER_ID);

        TrainingPurchasedEvent actual = thenTrainingPurchasedEventPublished();
        assertThatTrainingPurchasedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private TrainingPurchasedEvent thenTrainingPurchasedEventPublished() {
        ArgumentCaptor<TrainingPurchasedEvent> captor = ArgumentCaptor.forClass(TrainingPurchasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyConfirmed() {
        givenOrder().confirmed(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CONFIRMED");
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyRejected() {
        givenOrder().rejected(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptOrderCancellationIfOrderAlreadyTerminated() {
        givenOrder().createdMinutesAgo(20).terminated(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.cancel(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already TERMINATED");
    }

    @Test
    void shouldCancelOrder() {
        givenOrder().initiated(ORDER_ID);

        service.cancel(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isCancelled();
    }

    private void givenPayment(PaymentResponse paymentResponse) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(ORDER_ID)
                .participantId(PARTICIPANT_ID)
                .price(AMOUNT, CURRENCY)
                .build();
        given(paymentGateway.pay(paymentRequest)).willReturn(paymentResponse);
    }

    @Test
    void shouldPublishOrderCancelledEventWhenOrderCancelled() {
        givenOrder().initiated(ORDER_ID);

        service.cancel(ORDER_ID);

        OrderCancelledEvent actual = thenOrderCancelledEventPublished();
        assertThatOrderCancelledEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderCancelledEvent thenOrderCancelledEventPublished() {
        ArgumentCaptor<OrderCancelledEvent> captor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyConfirmed() {
        givenOrder().confirmed(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CONFIRMED");
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyRejected() {
        givenOrder().rejected(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptOrderTerminationIfOrderAlreadyCancelled() {
        givenOrder().cancelled(ORDER_ID);

        OrderInFinalStateException actual = assertThrows(OrderInFinalStateException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order: " + ORDER_ID + " already CANCELLED");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldInterruptOrderTerminationIfOrderTooNew(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        OrderTerminationNotYetPermittedException actual = assertThrows(OrderTerminationNotYetPermittedException.class, () -> service.terminate(ORDER_ID));

        assertThat(actual).hasMessage("Order with id " + ORDER_ID + " cannot be terminated yet.");
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 13, 20, 100})
    void shouldTerminateOrder(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.terminate(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isTerminated();
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 13, 20, 100})
    void shouldPublishOrderTerminatedEventWhenOrderTerminated(int minutes) {
        givenOrder().createdMinutesAgo(minutes).initiated(ORDER_ID);

        service.terminate(ORDER_ID);

        OrderTerminatedEvent actual = thenOrderTerminatedEventPublished();
        assertThatOrderTerminatedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderTerminatedEvent thenOrderTerminatedEventPublished() {
        ArgumentCaptor<OrderTerminatedEvent> captor = ArgumentCaptor.forClass(OrderTerminatedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());

        return captor.getValue();
    }

    private GivenOrder givenOrder() {
        return given.order()
                .trainingId(TRAINING_ID)
                .participantId(PARTICIPANT_ID)
                .amount(AMOUNT)
                .currency(CURRENCY);
    }
}