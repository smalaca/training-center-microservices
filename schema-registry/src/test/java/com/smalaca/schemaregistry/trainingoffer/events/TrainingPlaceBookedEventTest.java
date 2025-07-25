package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPlaceBookedEventTest {
    @Test
    void shouldCreateTrainingPlaceBookedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingId = UUID.randomUUID();

        TrainingPlaceBookedEvent event = new TrainingPlaceBookedEvent(eventId, offerId, participantId, trainingId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
        assertThat(event.trainingId()).isEqualTo(trainingId);
    }
}