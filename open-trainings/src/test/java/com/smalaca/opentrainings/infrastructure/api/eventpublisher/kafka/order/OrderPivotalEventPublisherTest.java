package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderQueryService;
import com.smalaca.opentrainings.query.order.OrderView;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
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
    private static final String TOPIC_NAME = "test-training-purchased-topic";
    private static final UUID ORDER_ID = randomId();
    private static final UUID OFFER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();

    private final OrderQueryService orderQueryService = mock(OrderQueryService.class);
    private final KafkaTemplate<String, TrainingPurchasedPivotalEvent> kafkaTemplate = mock(KafkaTemplate.class);
    private final OrderPivotalEventPublisher publisher = new OrderPivotalEventPublisher(orderQueryService, kafkaTemplate, TOPIC_NAME);

    @Test
    void shouldPublishTrainingPurchasedPivotalEvent() {
        given(orderQueryService.findById(ORDER_ID)).willReturn(Optional.of(new OrderView()));
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
        then(kafkaTemplate).should().send(eq(TOPIC_NAME), captor.capture());

        return assertThatTrainingPurchasedPivotalEvent(captor.getValue());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderNotFoundAfterTrainingPurchasedEventConsumed() {
        given(orderQueryService.findById(ORDER_ID)).willReturn(Optional.empty());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> publisher.consume(trainingPurchasedEvent()));

        assertThat(actual).hasMessage("Order not found for ID: " + ORDER_ID);
    }

    @Test
    void shouldPublishNoEventWhenOrderNotFoundAfterTrainingPurchasedEventConsumed() {
        given(orderQueryService.findById(ORDER_ID)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> publisher.consume(trainingPurchasedEvent()));

        then(kafkaTemplate).should(never()).send(any(), any());
    }

    private TrainingPurchasedEvent trainingPurchasedEvent() {
        return TrainingPurchasedEvent.create(ORDER_ID, OFFER_ID, TRAINING_ID, PARTICIPANT_ID);
    }
}