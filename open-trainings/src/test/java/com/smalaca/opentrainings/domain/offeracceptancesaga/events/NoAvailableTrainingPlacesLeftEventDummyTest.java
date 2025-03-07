package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.junit.jupiter.api.Assertions.*;

class NoAvailableTrainingPlacesLeftEventDummyTest {
    @Test
    void shouldEventBeAccepted() {
        NoAvailableTrainingPlacesLeftEvent event = new NoAvailableTrainingPlacesLeftEvent(null, null, null, null);

        assertDoesNotThrow(() -> event.accept(new OfferAcceptanceSaga(randomId()), null));
    }
}