package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotAvailableOfferAcceptanceRequestedEvent(EventId eventId, UUID offerId, String status) implements OfferEvent, OfferAcceptanceSagaEvent {
    public static NotAvailableOfferAcceptanceRequestedEvent nextAfter(BeginOfferAcceptanceCommand command, String status) {
        return new NotAvailableOfferAcceptanceRequestedEvent(command.commandId().nextEventId(), command.offerId(), status);
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}
