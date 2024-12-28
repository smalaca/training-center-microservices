package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.test.type.IntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
@Import(JpaOutboxEventRepository.class)
class JpaOutboxEventRepositoryTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private JpaOutboxEventRepository repository;

    @Autowired
    private SpringOutboxEventCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    void deleteAllEvents() {
        springRepository.deleteAll();
    }

    @Test
    void shouldPublishTrainingPurchased() {
        TrainingPurchasedEvent event = randomTrainingPurchasedEvent();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.publish(event));

        assertThat(repository.findAll())
                .hasSize(1)
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderRejected() {
        OrderRejectedEvent event = randomOrderRejectedEvent();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.publish(event));

        assertThat(repository.findAll())
                .hasSize(1)
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, event));
    }

    @Test
    void shouldPublishOrderCancelled() {
        OrderCancelledEvent event = randomOrderCancelledEvent();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.publish(event));

        assertThat(repository.findAll())
                .hasSize(1)
                .anySatisfy(actual -> assertOrderCancelledEventSaved(actual, event));
    }

    @Test
    void shouldFindAllEvents() {
        TrainingPurchasedEvent eventOne = saved(randomTrainingPurchasedEvent());
        OrderRejectedEvent eventTwo = saved(randomOrderRejectedEvent());
        TrainingPurchasedEvent eventThree = saved(randomTrainingPurchasedEvent());
        TrainingPurchasedEvent eventFour = saved(randomTrainingPurchasedEvent());
        OrderCancelledEvent eventFive = saved(randomOrderCancelledEvent());
        OrderRejectedEvent eventSix = saved(randomOrderRejectedEvent());

        assertThat(repository.findAll())
                .hasSize(6)
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventOne))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventTwo))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventThree))
                .anySatisfy(actual -> assertTrainingPurchasedEventSaved(actual, eventFour))
                .anySatisfy(actual -> assertOrderCancelledEventSaved(actual, eventFive))
                .anySatisfy(actual -> assertOrderRejectedEventSaved(actual, eventSix));
    }

    private <T extends OrderEvent> T saved(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            return event;
        });
    }

    private OrderRejectedEvent randomOrderRejectedEvent() {
        return new OrderRejectedEvent(newEventId(), randomId(), FAKER.lorem().paragraph());
    }

    private TrainingPurchasedEvent randomTrainingPurchasedEvent() {
        return new TrainingPurchasedEvent(newEventId(), randomId(), randomId(), randomId());
    }

    private OrderCancelledEvent randomOrderCancelledEvent() {
        return new OrderCancelledEvent(newEventId(), randomId(), randomId(), randomId());
    }

    private void assertOrderRejectedEventSaved(OutboxEvent actual, OrderRejectedEvent expected) {
        assertThat(actual.getEventId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getType()).isEqualTo("OrderRejectedEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"reason\" : \"" + expected.reason());
    }

    private void assertTrainingPurchasedEventSaved(OutboxEvent actual, TrainingPurchasedEvent expected) {
        assertThat(actual.getEventId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getType()).isEqualTo("TrainingPurchasedEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"trainingId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
    }

    private void assertOrderCancelledEventSaved(OutboxEvent actual, OrderCancelledEvent expected) {
        assertThat(actual.getEventId()).isEqualTo(expected.eventId().eventId());
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected.eventId().creationDateTime());
        assertThat(actual.getType()).isEqualTo("OrderCancelledEvent");
        assertThat(actual.getPayload())
                .contains("\"orderId\" : \"" + expected.orderId())
                .contains("\"trainingId\" : \"" + expected.trainingId())
                .contains("\"participantId\" : \"" + expected.participantId());
    }
}