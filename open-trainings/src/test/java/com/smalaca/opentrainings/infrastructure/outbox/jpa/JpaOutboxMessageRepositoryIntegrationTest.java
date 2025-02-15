package com.smalaca.opentrainings.infrastructure.outbox.jpa;

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
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent.trainingNoLongerAvailable;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
@Import(JpaOutboxEventRepositoryFactory.class)
class JpaOutboxMessageRepositoryIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private JpaOutboxEventRepository repository;

    @Autowired
    private SpringOutboxEventCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final List<UUID> eventIds = new ArrayList<>();

    @AfterEach
    void deleteAllEvents() {
        if (!eventIds.isEmpty()) {
            springRepository.deleteAllById(eventIds);
        }
    }

    @Test
    void shouldPublishOfferRejected() {
        repository.publish(trainingNoLongerAvailable(randomId()));
    }

    @Test
    void shouldPublishTrainingPurchased() {
        TrainingPurchasedEvent event = randomTrainingPurchasedEvent();

        publish(event);

        assertThat(repository.findAll()).anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderRejected() {
        OrderRejectedEvent event = randomOrderRejectedEvent();

        publish(event);

        assertThat(repository.findAll()).anySatisfy(actual -> assertOrderRejectedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderCancelled() {
        OrderCancelledEvent event = randomOrderCancelledEvent();

        publish(event);

        assertThat(repository.findAll()).anySatisfy(actual -> assertOrderCancelledEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderTerminatedEvent() {
        OrderTerminatedEvent event = randomOrderTerminatedEvent();

        publish(event);

        assertThat(repository.findAll()).anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, event));
    }

    @Test
    void shouldFindAllEvents() {
        TrainingPurchasedEvent eventOne = publish(randomTrainingPurchasedEvent());
        OrderRejectedEvent eventTwo = publish(randomOrderRejectedEvent());
        TrainingPurchasedEvent eventThree = publish(randomTrainingPurchasedEvent());
        TrainingPurchasedEvent eventFour = publish(randomTrainingPurchasedEvent());
        OrderCancelledEvent eventFive = publish(randomOrderCancelledEvent());
        OrderRejectedEvent eventSix = publish(randomOrderRejectedEvent());
        OrderTerminatedEvent eventSeven = publish(randomOrderTerminatedEvent());
        OrderTerminatedEvent eventEight = publish(randomOrderTerminatedEvent());

        assertThat(repository.findAll())
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventOne))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventTwo))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventThree))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventFour))
                .anySatisfy(actual -> assertOrderCancelledEventSaved(actual, eventFive))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventSix))
                .anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, eventSeven))
                .anySatisfy(actual -> assertOrderTerminatedEventSaved(actual, eventEight));
    }

    private <T extends OrderEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            eventIds.add(event.eventId().eventId());
            return event;
        });
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