package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

public record AcceptOfferCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email, String discountCode) implements OfferAcceptanceSagaCommand {
    public static AcceptOfferCommand nextAfter(OfferAcceptanceRequestedEvent event) {
        return new AcceptOfferCommand(event.eventId().nextCommandId(), event.offerId(), event.firstName(), event.lastName(), event.email(), event.discountCode());
    }
}
