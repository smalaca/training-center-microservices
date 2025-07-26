package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoAvailableTrainingPlacesLeftEventTest {
    @Test
    void shouldCreateNoAvailableTrainingPlacesLeftEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingOfferId = UUID.randomUUID();

        NoAvailableTrainingPlacesLeftEvent event = new NoAvailableTrainingPlacesLeftEvent(eventId, offerId, participantId, trainingOfferId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
        assertThat(event.trainingOfferId()).isEqualTo(trainingOfferId);
    }
}