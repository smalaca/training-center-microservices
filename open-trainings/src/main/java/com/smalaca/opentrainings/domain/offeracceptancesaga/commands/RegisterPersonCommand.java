package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

public record RegisterPersonCommand(CommandId commandId, UUID offerId, String firstName, String lastName, String email) implements OfferAcceptanceSagaCommand{
    public static RegisterPersonCommand nextAfter(OfferAcceptanceRequestedEvent event) {
        return new RegisterPersonCommand(event.eventId().nextCommandId(), event.offerId(), event.firstName(), event.lastName(), event.email());
    }
}
