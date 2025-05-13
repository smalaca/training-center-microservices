package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.contracts.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRegisteredEventTest {
    @Test
    void shouldCreatePersonRegisteredEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();

        PersonRegisteredEvent event = new PersonRegisteredEvent(eventId, offerId, participantId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
    }
}
