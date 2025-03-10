package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NoAvailableTrainingPlacesLeftEventDummyTest {
    @Test
    void shouldEventBeAccepted() {
        NoAvailableTrainingPlacesLeftEvent event = new NoAvailableTrainingPlacesLeftEvent(newEventId(), randomId(), randomId(), randomId());

        assertDoesNotThrow(() -> event.accept(new OfferAcceptanceSaga(randomId()), null));
    }
}