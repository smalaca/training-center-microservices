package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OfferAcceptanceSagaEvent {
    EventId eventId();
    UUID offerId();
    void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt);
}
