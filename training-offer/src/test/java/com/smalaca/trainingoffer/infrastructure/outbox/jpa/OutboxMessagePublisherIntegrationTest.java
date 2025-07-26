package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import com.smalaca.trainingoffer.infrastructure.api.eventlistener.spring.TrainingOfferDraftListener;
import com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft.TrainingOfferDraftEventPublisher;
import com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft.TrainingOfferEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
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
    private TrainingOfferDraftEventPublisher trainingOfferDraftEventPublisher;

    @MockBean
    private TrainingOfferDraftListener trainingOfferDraftListener;

    @MockBean
    private TrainingOfferEventPublisher trainingOfferEventPublisher;

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
        TrainingOfferPublishedEvent trainingOfferPublishedEventOne = randomTrainingOfferPublishedEvent();
        TrainingOfferPublishedEvent trainingOfferPublishedEventTwo = randomTrainingOfferPublishedEvent();
        TrainingPriceNotChangedEvent trainingPriceNotChangedEvent = randomTrainingPriceNotChangedEvent();
        TrainingPriceChangedEvent trainingPriceChangedEvent = randomTrainingPriceChangedEvent();
        NoAvailableTrainingPlacesLeftEvent noAvailableTrainingPlacesLeftEvent = randomNoAvailableTrainingPlacesLeftEvent();
        TrainingPlaceBookedEvent trainingPlaceBookedEvent = randomTrainingPlaceBookedEvent();
        notPublished(trainingOfferPublishedEventOne);
        published(randomTrainingOfferPublishedEvent());
        notPublished(trainingOfferPublishedEventTwo);
        notPublished(trainingPriceNotChangedEvent);
        published(randomTrainingPriceNotChangedEvent());
        notPublished(trainingPriceChangedEvent);
        published(randomTrainingPriceChangedEvent());
        published(randomNoAvailableTrainingPlacesLeftEvent());
        notPublished(noAvailableTrainingPlacesLeftEvent);
        notPublished(trainingPlaceBookedEvent);
        published(randomTrainingPlaceBookedEvent());

        await()
                .untilAsserted(() -> {
                    assertThat(listener.trainingOfferPublishedEvents).contains(trainingOfferPublishedEventOne, trainingOfferPublishedEventTwo);
                    assertThat(listener.trainingPriceNotChangedEvents).contains(trainingPriceNotChangedEvent);
                    assertThat(listener.trainingPriceChangedEvents).contains(trainingPriceChangedEvent);
                    assertThat(listener.noAvailableTrainingPlacesLeftEvents).contains(noAvailableTrainingPlacesLeftEvent);
                    assertThat(listener.trainingPlaceBookedEvents).contains(trainingPlaceBookedEvent);
                });
    }

    @Test
    void shouldMarkOutboxEventsAsPublished() {
        notPublished(randomTrainingOfferPublishedEvent());
        published(randomTrainingOfferPublishedEvent());
        notPublished(randomTrainingPriceNotChangedEvent());
        published(randomTrainingPriceNotChangedEvent());
        notPublished(randomTrainingPriceChangedEvent());
        published(randomTrainingPriceChangedEvent());
        notPublished(randomNoAvailableTrainingPlacesLeftEvent());
        published(randomNoAvailableTrainingPlacesLeftEvent());
        notPublished(randomTrainingPlaceBookedEvent());
        published(randomTrainingPlaceBookedEvent());

        await()
                .untilAsserted(() -> {
                    assertThat(repository.findAll())
                            .hasSize(10)
                            .allSatisfy(actual -> assertThat(actual.isPublished()).isTrue());
                });
    }

    private TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100.00), "USD", 5, 20,
                LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(17, 0));
    }
    
    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(EventId.newEventId(), UUID.randomUUID(), UUID.randomUUID());
    }
    
    private TrainingPriceChangedEvent randomTrainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(EventId.newEventId(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(150.00), "EUR");
    }
    
    private NoAvailableTrainingPlacesLeftEvent randomNoAvailableTrainingPlacesLeftEvent() {
        return new NoAvailableTrainingPlacesLeftEvent(EventId.newEventId(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }
    
    private TrainingPlaceBookedEvent randomTrainingPlaceBookedEvent() {
        return new TrainingPlaceBookedEvent(EventId.newEventId(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    private void published(TrainingOfferPublishedEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(TrainingOfferPublishedEvent event) {
        notPublished(event.eventId(), event);
    }
    
    private void published(TrainingOfferEvent event) {
        published(event.eventId(), event);
    }

    private void notPublished(TrainingOfferEvent event) {
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
