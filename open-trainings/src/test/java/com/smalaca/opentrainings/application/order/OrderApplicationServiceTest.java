package com.smalaca.opentrainings.application.order;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderAssertion.assertThatOrder;
import static com.smalaca.opentrainings.domain.order.events.OrderRejectedEventAssertion.assertThatOrderRejectedEvent;
import static com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEventAssertion.assertThatTrainingPurchasedEvent;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceTest {
    private static final Faker FAKER = new Faker();
    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID TRAINING_ID = UUID.randomUUID();
    private static final UUID PARTICIPANT_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = now();

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
        givenOrderCreatedAt(minutes);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isRejected();
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldPublishOrderRejectedWhenOlderThanTenMinutes(int minutes) {
        givenOrderCreatedAt(minutes);

        service.confirm(ORDER_ID);

        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
        assertThatOrderRejectedEvent(actual)
                .hasOrderId(ORDER_ID)
                .hasReason("Order expired");
    }

//    @Test
//    void shouldRejectOrderWhenPaymentFailed() {
//
//        BigDecimal price = BigDecimal.valueOf(FAKER.number().numberBetween(100l, 3000l)); // to order
//        String currency = FAKER.currency().toString();
//
//        PaymentDto.builder()
//                .withOrderId(ORDER_ID)
//                .withPrice(price)
//                .withPersonalData(PersonalData.builder()
//                        .withEmailAddress()
//                        .withFirstname()
//                        .withSurname()
//                        .build())
//        //        new PaymentDto()
////        given(paymentGateway.pa).willReturn();
//        givenOrder();
//
//        service.confirm(ORDER_ID);
//
//        Order actual = thenOrderSaved();
//        assertThatOrder(actual).isRejected();
//    }
//
//    @Test
//    void shouldPublishOrderRejectedWhenPaymentFailed() {
//        givenOrder();
//
//        service.confirm(ORDER_ID);
//
//        OrderRejectedEvent actual = thenOrderRejectedEventPublished();
//        assertThatOrderRejectedEvent(actual)
//                .hasId(ORDER_ID)
//                .hasReason("Could not complete payment.");
//    }

    private OrderRejectedEvent thenOrderRejectedEventPublished() {
        ArgumentCaptor<OrderRejectedEvent> captor = ArgumentCaptor.forClass(OrderRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldConfirmOrder(int minutes) {
        givenOrderCreatedAt(minutes);

        service.confirm(ORDER_ID);

        Order actual = thenOrderSaved();
        assertThatOrder(actual).isConfirmed();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldPublishTrainingPurchasedWhenOrderConfirmed(int minutes) {
        givenOrderCreatedAt(minutes);

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

    private Order thenOrderSaved() {
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(captor.capture());

        return captor.getValue();
    }

    private void givenOrderCreatedAt(int minutes) {
        LocalDateTime creationDateTime = moreThanMinutesAgo(minutes);
        Order order = orderFactory.orderCreatedAt(ORDER_ID, TRAINING_ID, PARTICIPANT_ID, creationDateTime);

        given(orderRepository.findById(ORDER_ID)).willReturn(order);
    }

    private LocalDateTime moreThanMinutesAgo(int minutes) {
        return NOW.minusMinutes(minutes);
    }
}