package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingPlaceBookedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingOfferId) implements OfferAcceptanceSagaEvent {
    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}