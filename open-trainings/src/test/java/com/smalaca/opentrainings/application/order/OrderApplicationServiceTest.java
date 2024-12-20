package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
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
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion.assertThatOrderRejectedEvent;
import static com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEventAssertion.assertThatTrainingPurchasedEvent;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.failed;
import static com.smalaca.opentrainings.domain.paymentgateway.PaymentResponse.successful;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceTest {
    private static final String CURRENCY = randomCurrency();
    private static final BigDecimal AMOUNT = randomAmount();
    private static final UUID ORDER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final LocalDateTime NOW = now();
    private static final int ONE_MINUTE = 1;

    private final OrderTestFactory orderFactory = OrderTestFactory.orderTestFactory();

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final PaymentGateway paymentGateway = mock(PaymentGateway.class);
    private final Clock clock = mock(Clock.class);
    private final OrderApplicationService service = new OrderApplicationService(orderRepository, eventRegistry, paymentGateway, clock);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(NOW);
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOrderWhenOlderThanTenMinutes(int minutes) {
        givenOrderCreatedAgoMinutes(minutes);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldPublishOrderRejectedWhenOlderThanTenMinutes(int minutes) {
        givenOrderCreatedAgoMinutes(minutes);

        service.confirm(ORDER_ID);

        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
        assertThatOrderRejectedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasReason("Order expired");
    }

    @Test
    void shouldRejectOrderWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder();

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @Test
    void shouldPublishOrderRejectedWhenPaymentFailed() {
        givenPayment(failed());
        givenOrder();

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
        givenOrderCreatedAgoMinutes(minutes);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isConfirmed();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldPublishTrainingPurchasedWhenOrderConfirmed(int minutes) {
        givenPayment(successful());
        givenOrderCreatedAgoMinutes(minutes);

        service.confirm(ORDER_ID);

        TrainingPurchasedEvent actual = thenTrainingPurchasedEventPublished();
        assertThatTrainingPurchasedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private void givenPayment(PaymentResponse paymentResponse) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(ORDER_ID)
                .participantId(PARTICIPANT_ID)
                .price(AMOUNT, CURRENCY)
                .build();
        given(paymentGateway.pay(paymentRequest)).willReturn(paymentResponse);
    }

    private TrainingPurchasedEvent thenTrainingPurchasedEventPublished() {
        ArgumentCaptor<TrainingPurchasedEvent> captor = ArgumentCaptor.forClass(TrainingPurchasedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());

        return captor.getValue();
    }

    private void givenOrder() {
        givenOrderCreatedAgoMinutes(ONE_MINUTE);
    }

    private void givenOrderCreatedAgoMinutes(int minutes) {
        Order order = orderFactory.orderCreatedAt(OrderTestDto.builder()
                .orderId(ORDER_ID)
                .trainingId(TRAINING_ID)
                .participantId(PARTICIPANT_ID)
                .amount(AMOUNT)
                .currency(CURRENCY)
                .creationDateTime(NOW.minusMinutes(minutes)));

        given(orderRepository.findById(ORDER_ID)).willReturn(order);
    }

}