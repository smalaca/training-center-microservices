package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
class SpringOutboxEventCrudRepositoryIntegrationTest {
    @Autowired
    private SpringOutboxEventCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private OutboxEventFactory factory;

    @BeforeEach
    void initOutboxEventFactory() {
        factory = new OutboxEventFactory(objectMapper);
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldFindOnlyNotPublishOutboxEvents() {
        OutboxEvent eventOne = notPublished(OfferRejectedEvent.expired(randomId()));
        published(OfferRejectedEvent.expired(randomId()));
        published(OrderRejectedEvent.expired(randomId()));
        OutboxEvent eventFour = notPublished(OrderRejectedEvent.expired(randomId()));
        OutboxEvent eventFive = notPublished(OrderRejectedEvent.expired(randomId()));

        List<OutboxEvent> actual = repository.findByIsPublishedFalse();

        assertThat(actual).containsExactlyInAnyOrder(eventOne, eventFour, eventFive);
    }

    private void published(OfferRejectedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxEvent notPublished(OfferRejectedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(OrderRejectedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxEvent notPublished(OrderRejectedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        OutboxEvent outboxEvent = factory.create(eventId, event);
        outboxEvent.published();
        repository.save(outboxEvent);
    }

    private OutboxEvent notPublished(EventId eventId, Object event) {
        OutboxEvent outboxEvent = factory.create(eventId, event);
        repository.save(outboxEvent);
        return outboxEvent;
    }
}