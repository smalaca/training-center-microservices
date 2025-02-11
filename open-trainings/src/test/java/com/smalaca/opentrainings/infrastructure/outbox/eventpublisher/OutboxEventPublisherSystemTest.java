package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.OutboxEvent;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.OutboxEventTestFactory;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.SpringOutboxEventCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SystemTest
@TestPropertySource(properties = "scheduled.outbox.event.rate=100")
class OutboxEventPublisherSystemTest {
    @Autowired
    private SpringOutboxEventCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxEventTestListener listener;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private OutboxEventTestFactory factory;

    @BeforeEach
    void initFactory() {
        factory = new OutboxEventTestFactory(objectMapper);
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldPublishOnlyNotPublishedOutboxEvents() {
        OfferRejectedEvent eventOne = OfferRejectedEvent.expired(randomId());
        OrderRejectedEvent eventTwo = OrderRejectedEvent.expired(randomId());
        OrderRejectedEvent eventThree = OrderRejectedEvent.expired(randomId());
        notPublished(eventOne);
        published(OfferRejectedEvent.expired(randomId()));
        published(OrderRejectedEvent.expired(randomId()));
        notPublished(eventTwo);
        notPublished(eventThree);

        await()
                .untilAsserted(() -> {
                    assertThat(listener.offerRejectedEvents).contains(eventOne);
                    assertThat(listener.orderRejectedEvents).contains(eventTwo, eventThree);
                });
    }

    @Test
    void shouldMarkOutboxEventsAsPublished() {
        notPublished(OfferRejectedEvent.expired(randomId()));
        published(OfferRejectedEvent.expired(randomId()));
        published(OrderRejectedEvent.expired(randomId()));
        notPublished(OrderRejectedEvent.expired(randomId()));
        notPublished(OrderRejectedEvent.expired(randomId()));

        await()
                .untilAsserted(() -> {
                    assertThat(repository.findAll())
                            .hasSize(5)
                            .allSatisfy(actual -> assertThat(actual.isPublished()).isTrue());
                });
    }

    private void published(OfferRejectedEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(OfferRejectedEvent event) {
        notPublished(event.eventId(), event);
    }

    private void published(OrderRejectedEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(OrderRejectedEvent event) {
        notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxEvent outboxEvent = factory.create(eventId, event);
            outboxEvent.published();
            repository.save(outboxEvent);
        });
    }

    private void notPublished(EventId eventId, Object event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxEvent outboxEvent = factory.create(eventId, event);
            repository.save(outboxEvent);
        });
    }
}