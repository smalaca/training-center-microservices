package com.smalaca.opentrainings.infrastructure.outbox.jpa;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
class SpringOutboxMessageCrudRepositoryIntegrationTest {
    @Autowired
    private SpringOutboxEventCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private OutboxEventFactory factory;

    private final List<UUID> eventIds = new ArrayList<>();

    @BeforeEach
    void initOutboxEventFactory() {
        factory = new OutboxEventFactory(objectMapper);
    }

    @AfterEach
    void deleteAllEvents() {
        if (!eventIds.isEmpty()) {
            repository.deleteAllById(eventIds);
        }
    }

    @Test
    void shouldFindOnlyNotPublishOutboxEvents() {
        OutboxMessage eventOne = notPublished(OfferRejectedEvent.expired(randomId()));
        published(OfferRejectedEvent.expired(randomId()));
        published(OrderRejectedEvent.expired(randomId()));
        OutboxMessage eventFour = notPublished(OrderRejectedEvent.expired(randomId()));
        OutboxMessage eventFive = notPublished(OrderRejectedEvent.expired(randomId()));

        List<OutboxMessage> actual = repository.findByIsPublishedFalse();

        assertThat(actual).containsExactlyInAnyOrder(eventOne, eventFour, eventFive);
    }

    private void published(OfferRejectedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxMessage notPublished(OfferRejectedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(OrderRejectedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxMessage notPublished(OrderRejectedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.create(eventId, event);
        outboxMessage.published();
        eventIds.add(eventId.eventId());
        repository.save(outboxMessage);
    }

    private OutboxMessage notPublished(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.create(eventId, event);
        eventIds.add(eventId.eventId());
        repository.save(outboxMessage);
        return outboxMessage;
    }
}