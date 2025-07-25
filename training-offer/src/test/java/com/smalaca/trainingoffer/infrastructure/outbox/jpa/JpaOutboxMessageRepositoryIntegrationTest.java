package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.test.type.SpringBootIntegrationTest;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingoffer.infrastructure.outbox.jpa.OutboxMessageAssertion.assertThatOutboxMessage;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootIntegrationTest
@Import(JpaOutboxMessageRepositoryFactory.class)
class JpaOutboxMessageRepositoryIntegrationTest {

    @Autowired
    private JpaOutboxMessageRepository repository;

    @Autowired
    private SpringOutboxMessageCrudRepository springRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final List<UUID> messagesIds = new ArrayList<>();

    @AfterEach
    void deleteAllEvents() {
        if (!messagesIds.isEmpty()) {
            springRepository.deleteAllById(messagesIds);
        }
    }

    @Test
    void shouldFindAllOutboxMessages() {
        TrainingOfferPublishedEvent trainingOfferPublishedEvent = publish(randomTrainingOfferPublishedEvent());
        TrainingPriceChangedEvent trainingPriceChangedEvent = publishTrainingOfferEvent(randomTrainingPriceChangedEvent());
        TrainingPriceNotChangedEvent trainingPriceNotChangedEvent = publishTrainingOfferEvent(randomTrainingPriceNotChangedEvent());

        assertThat(springRepository.findAll())
                .anySatisfy(actual -> assertTrainingPriceNotChangedEventSaved(actual, trainingPriceNotChangedEvent))
                .anySatisfy(actual -> assertTrainingPriceChangedEventSaved(actual, trainingPriceChangedEvent))
                .anySatisfy(actual -> assertTrainingOfferPublishedEventSaved(actual, trainingOfferPublishedEvent));
    }

    private <T extends TrainingOfferPublishedEvent> T publish(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }
    
    private <T extends TrainingOfferEvent> T publishTrainingOfferEvent(T event) {
        return transactionTemplate.execute(transactionStatus -> {
            repository.publish(event);
            messagesIds.add(event.eventId().eventId());
            return event;
        });
    }

    private TrainingOfferPublishedEvent randomTrainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(100.00), "USD", 5, 20,
                LocalDate.of(2023, 10, 15), LocalDate.of(2023, 10, 20),
                LocalTime.of(9, 0), LocalTime.of(17, 0));
    }
    
    private TrainingPriceChangedEvent randomTrainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(
                EventId.newEventId(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(150.00),
                "EUR");
    }
    
    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(
                EventId.newEventId(),
                UUID.randomUUID(),
                UUID.randomUUID());
    }

    private void assertTrainingOfferPublishedEventSaved(OutboxMessage actual, TrainingOfferPublishedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }
    
    private void assertTrainingPriceChangedEventSaved(OutboxMessage actual, TrainingPriceChangedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }
    
    private void assertTrainingPriceNotChangedEventSaved(OutboxMessage actual, TrainingPriceNotChangedEvent expected) {
        assertThatOutboxMessage(actual)
                .hasMessageId(expected.eventId().eventId())
                .hasOccurredOn(expected.eventId().creationDateTime())
                .hasMessageType("com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent")
                .hasPayloadThatContainsAllDataFrom(expected);
    }
}