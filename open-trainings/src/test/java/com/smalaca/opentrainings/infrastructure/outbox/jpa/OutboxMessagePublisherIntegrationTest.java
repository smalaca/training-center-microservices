package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@IntegrationTest
@TestPropertySource(properties = "scheduled.outbox.message.rate=100")
class OutboxMessagePublisherIntegrationTest {
    @Autowired
    private SpringOutboxMessageCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutboxMessageTestListener listener;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private OfferAcceptanceSagaEngine engine;

    private OutboxMessageMapper factory;

    private final List<UUID> messageIds = new ArrayList<>();

    @BeforeEach
    void initFactory() {
        factory = new OutboxMessageMapper(objectMapper);
    }

    @AfterEach
    void deleteAllEvents() {
        if (!messageIds.isEmpty()) {
            repository.deleteAllById(messageIds);
        }
    }

    @Test
    void shouldPublishOnlyNotPublishedOutboxEvents() {
        OfferRejectedEvent eventOne = new OfferRejectedEvent(newEventId(), randomId(), "Dummy reason");
        OrderRejectedEvent eventTwo = OrderRejectedEvent.expired(randomId());
        OrderRejectedEvent eventThree = OrderRejectedEvent.expired(randomId());
        notPublished(eventOne);
        published(new OfferRejectedEvent(newEventId(), randomId(), "Dummy message"));
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
        notPublished(new OfferRejectedEvent(newEventId(), randomId(), "Dummy reason"));
        published(new OfferRejectedEvent(newEventId(), randomId(), "Dummy reason"));
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
            OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
            outboxMessage.published();
            messageIds.add(eventId.eventId());
            repository.save(outboxMessage);
        });
    }

    private void notPublished(EventId eventId, Object event) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
            messageIds.add(eventId.eventId());
            repository.save(outboxMessage);
        });
    }
}