package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record TrainingPriceNotChangedEvent(EventId eventId, UUID offerId, UUID trainingId) implements OfferAcceptanceSagaEvent {
    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}