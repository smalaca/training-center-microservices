package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExpiredOfferAcceptanceRequestedEvent(EventId eventId, UUID offerId) implements OfferEvent, OfferAcceptanceSagaEvent {
    public static OfferEvent nextAfter(BeginOfferAcceptanceCommand command) {
        return new ExpiredOfferAcceptanceRequestedEvent(command.commandId().nextEventId(), command.offerId());
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}
