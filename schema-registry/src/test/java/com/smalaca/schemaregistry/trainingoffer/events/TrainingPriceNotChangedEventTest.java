package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPriceNotChangedEventTest {
    @Test
    void shouldCreateTrainingPriceNotChangedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID trainingOfferId = UUID.randomUUID();

        TrainingPriceNotChangedEvent event = new TrainingPriceNotChangedEvent(eventId, offerId, trainingOfferId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.trainingOfferId()).isEqualTo(trainingOfferId);
    }
}