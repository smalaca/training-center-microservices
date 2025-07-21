package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootIntegrationTest
class SpringOutboxMessageCrudRepositoryIntegrationTest {
    @Autowired
    private SpringOutboxMessageCrudRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private OutboxMessageMapper factory;

    private final List<UUID> messageIds = new ArrayList<>();

    @BeforeEach
    void initOutboxEventFactory() {
        factory = new OutboxMessageMapper(objectMapper);
    }

    @AfterEach
    void deleteAllEvents() {
        if (!messageIds.isEmpty()) {
            repository.deleteAllById(messageIds);
        }
    }

    @Test
    void shouldFindOnlyNotPublishedOutboxEvents() {
        OutboxMessage eventOne = notPublished(randomTrainingOfferPublishedEvent());
        published(randomTrainingOfferPublishedEvent());
        published(randomTrainingOfferPublishedEvent());
        OutboxMessage eventFour = notPublished(randomTrainingOfferPublishedEvent());
        OutboxMessage eventFive = notPublished(randomTrainingOfferPublishedEvent());

        List<OutboxMessage> actual = repository.findByIsPublishedFalse();

        assertThat(actual).containsExactlyInAnyOrder(eventOne, eventFour, eventFive);
    }

    private TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100.00), "USD", 5, 20,
                LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(17, 0));
    }

    private void published(TrainingOfferPublishedEvent event) {
        published(event.eventId(), event);
    }

    private OutboxMessage notPublished(TrainingOfferPublishedEvent event) {
        return notPublished(event.eventId(), event);
    }

    private void published(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
        outboxMessage.published();
        messageIds.add(eventId.eventId());
        repository.save(outboxMessage);
    }

    private OutboxMessage notPublished(EventId eventId, Object event) {
        OutboxMessage outboxMessage = factory.outboxMessage(eventId, event);
        messageIds.add(eventId.eventId());
        repository.save(outboxMessage);
        return outboxMessage;
    }
}