package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiscountCodeReturnedEvent(EventId eventId, UUID offerId, UUID participantId, String discountCode) implements OfferAcceptanceSagaEvent {
    @Override
    public void accept(OfferAcceptanceSaga saga, LocalDateTime consumedAt) {
        saga.accept(this, () -> consumedAt);
    }
}