package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.junit.jupiter.api.Assertions.*;

class DiscountCodeUsedEventDummyTest {
    @Test
    void shouldEventBeAccepted() {
        DiscountCodeUsedEvent event = new DiscountCodeUsedEvent(null, null, null, null, null, null, null, null);

        assertDoesNotThrow(() -> event.accept(new OfferAcceptanceSaga(randomId()), null));
    }
}