package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderCancelledPivotalEventAssertion.assertThatOrderCancelledPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderRejectedPivotalEventAssertion.assertThatOrderRejectedPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.OrderTerminatedPivotalEventAssertion.assertThatOrderTerminatedPivotalEvent;
import static com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order.TrainingPurchasedPivotalEventAssertion.assertThatTrainingPurchasedPivotalEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class OrderPivotalEventPublisherTest {
    private static final String TRAINING_PURCHASED_TOPIC = "test-training-purchased-topic";
    private static final String ORDER_REJECTED_TOPIC = "test-order-rejected-topic";
    private static final String ORDER_CANCELLED_TOPIC = "test-order-cancelled-topic";
    private static final String ORDER_TERMINATED_TOPIC = "test-order-terminated-topic";
    private static final UUID ORDER_ID = randomId();
    private static final UUID OFFER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();

    private final OrderQueryService orderQueryService = mock(OrderQueryService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final OrderPivotalEventPublisher publisher = new OrderPivotalEventPublisherFactory().createOrderPivotalEventPublisher(
            orderQueryService, kafkaTemplate, TRAINING_PURCHASED_TOPIC, ORDER_REJECTED_TOPIC, ORDER_TERMINATED_TOPIC, ORDER_CANCELLED_TOPIC);

    @Test
    void shouldPublishTrainingPurchasedPivotalEvent() {
        givenExistingOrder();
        TrainingPurchasedEvent event = trainingPurchasedEvent();

        publisher.consume(event);

        thenTrainingPurchasedPivotalEventPublished()
                .isNextAfter(event.eventId())
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private TrainingPurchasedPivotalEventAssertion thenTrainingPurchasedPivotalEventPublished() {
        ArgumentCaptor<TrainingPurchasedPivotalEvent> captor = ArgumentCaptor.forClass(TrainingPurchasedPivotalEvent.class);
        then(kafkaTemplate).should().send(eq(TRAINING_PURCHASED_TOPIC), captor.capture());

        return assertThatTrainingPurchasedPivotalEvent(captor.getValue());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderNotFoundAfterTrainingPurchasedEventConsumed() {
        givenNoExistingOrder();

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> publisher.consume(trainingPurchasedEvent()));

        assertThat(actual).hasMessage("Order not found for ID: " + ORDER_ID);
    }

    @Test
    void shouldPublishNoEventWhenOrderNotFoundAfterTrainingPurchasedEventConsumed() {
        givenNoExistingOrder();

        assertThrows(IllegalArgumentException.class, () -> publisher.consume(trainingPurchasedEvent()));

        then(kafkaTemplate).should(never()).send(any(), any());
    }

    private TrainingPurchasedEvent trainingPurchasedEvent() {
        return TrainingPurchasedEvent.create(ORDER_ID, OFFER_ID, TRAINING_ID, PARTICIPANT_ID);
    }

    @Test
    void shouldPublishOrderRejectedPivotalEvent() {
        givenExistingOrder();
        OrderRejectedEvent event = orderRejectedEvent();

        publisher.consume(event);

        thenOrderRejectedPivotalEventPublished()
                .isNextAfter(event.eventId())
                .hasOrderId(ORDER_ID)
                .hasReason(event.reason());
    }

    private OrderRejectedPivotalEventAssertion thenOrderRejectedPivotalEventPublished() {
        ArgumentCaptor<OrderRejectedPivotalEvent> captor = ArgumentCaptor.forClass(OrderRejectedPivotalEvent.class);
        then(kafkaTemplate).should().send(eq(ORDER_REJECTED_TOPIC), captor.capture());

        return assertThatOrderRejectedPivotalEvent(captor.getValue());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderNotFoundAfterOrderRejectedEventConsumed() {
        givenNoExistingOrder();

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderRejectedEvent()));

        assertThat(actual).hasMessage("Order not found for ID: " + ORDER_ID);
    }

    @Test
    void shouldPublishNoEventWhenOrderNotFoundAfterOrderRejectedEventConsumed() {
        givenNoExistingOrder();

        assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderRejectedEvent()));

        then(kafkaTemplate).should(never()).send(any(), any());
    }

    private OrderRejectedEvent orderRejectedEvent() {
        return OrderRejectedEvent.expired(ORDER_ID);
    }

    @Test
    void shouldPublishOrderTerminatedPivotalEvent() {
        givenExistingOrder();
        OrderTerminatedEvent event = orderTerminatedEvent();

        publisher.consume(event);

        thenOrderTerminatedPivotalEventPublished()
                .isNextAfter(event.eventId())
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderTerminatedPivotalEventAssertion thenOrderTerminatedPivotalEventPublished() {
        ArgumentCaptor<OrderTerminatedPivotalEvent> captor = ArgumentCaptor.forClass(OrderTerminatedPivotalEvent.class);
        then(kafkaTemplate).should().send(eq(ORDER_TERMINATED_TOPIC), captor.capture());

        return assertThatOrderTerminatedPivotalEvent(captor.getValue());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderNotFoundAfterOrderTerminatedEventConsumed() {
        givenNoExistingOrder();

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderTerminatedEvent()));

        assertThat(actual).hasMessage("Order not found for ID: " + ORDER_ID);
    }

    @Test
    void shouldPublishNoEventWhenOrderNotFoundAfterOrderTerminatedEventConsumed() {
        givenNoExistingOrder();

        assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderTerminatedEvent()));

        then(kafkaTemplate).should(never()).send(any(), any());
    }

    private OrderTerminatedEvent orderTerminatedEvent() {
        return OrderTerminatedEvent.create(ORDER_ID, OFFER_ID, TRAINING_ID, PARTICIPANT_ID);
    }

    @Test
    void shouldPublishOrderCancelledPivotalEvent() {
        givenExistingOrder();
        OrderCancelledEvent event = orderCancelledEvent();

        publisher.consume(event);

        thenOrderCancelledPivotalEventPublished()
                .isNextAfter(event.eventId())
                .hasOrderId(ORDER_ID)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private OrderCancelledPivotalEventAssertion thenOrderCancelledPivotalEventPublished() {
        ArgumentCaptor<OrderCancelledPivotalEvent> captor = ArgumentCaptor.forClass(OrderCancelledPivotalEvent.class);
        then(kafkaTemplate).should().send(eq(ORDER_CANCELLED_TOPIC), captor.capture());

        return assertThatOrderCancelledPivotalEvent(captor.getValue());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderNotFoundAfterOrderCancelledEventConsumed() {
        givenNoExistingOrder();

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderCancelledEvent()));

        assertThat(actual).hasMessage("Order not found for ID: " + ORDER_ID);
    }

    @Test
    void shouldPublishNoEventWhenOrderNotFoundAfterOrderCancelledEventConsumed() {
        givenNoExistingOrder();

        assertThrows(IllegalArgumentException.class, () -> publisher.consume(orderCancelledEvent()));

        then(kafkaTemplate).should(never()).send(any(), any());
    }

    private OrderCancelledEvent orderCancelledEvent() {
        return OrderCancelledEvent.create(ORDER_ID, OFFER_ID, TRAINING_ID, PARTICIPANT_ID);
    }

    private void givenExistingOrder() {
        given(orderQueryService.findById(ORDER_ID)).willReturn(Optional.of(new OrderView()));
    }

    private void givenNoExistingOrder() {
        given(orderQueryService.findById(ORDER_ID)).willReturn(Optional.empty());
    }
}