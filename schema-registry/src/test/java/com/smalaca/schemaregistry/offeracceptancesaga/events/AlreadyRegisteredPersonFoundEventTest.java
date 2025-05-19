package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlreadyRegisteredPersonFoundEventTest {
    @Test
    void shouldCreateAlreadyRegisteredPersonFoundEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();

        AlreadyRegisteredPersonFoundEvent event = new AlreadyRegisteredPersonFoundEvent(eventId, offerId, participantId);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
    }
}
