package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

public record BeginOfferAcceptanceCommand(CommandId commandId, UUID offerId) implements OfferAcceptanceSagaCommand {
    public static BeginOfferAcceptanceCommand nextAfter(OfferAcceptanceRequestedEvent event) {
        return new BeginOfferAcceptanceCommand(event.eventId().nextCommandId(), event.offerId());
    }
}
