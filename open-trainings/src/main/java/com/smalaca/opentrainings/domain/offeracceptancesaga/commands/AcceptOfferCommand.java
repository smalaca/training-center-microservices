package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;

import java.util.UUID;

public record AcceptOfferCommand(CommandId commandId, UUID offerId, UUID participantId, String firstName, String lastName, String email, String discountCode) implements OfferAcceptanceSagaCommand {
    public static AcceptOfferCommand nextAfter(OfferAcceptanceRequestedEvent event) {
        return new AcceptOfferCommand(event.eventId().nextCommandId(), event.offerId(), null, event.firstName(), event.lastName(), event.email(), event.discountCode());
    }

    public static AcceptOfferCommand nextAfter(PersonRegisteredEvent event, String discountCode) {
        return new AcceptOfferCommand(event.eventId().nextCommandId(), event.offerId(), event.participantId(), null, null, null, discountCode);
    }

    public static AcceptOfferCommand nextAfter(AlreadyRegisteredPersonFoundEvent event, String discountCode) {
        return new AcceptOfferCommand(event.eventId().nextCommandId(), event.offerId(), event.participantId(), null, null, null, discountCode);
    }
}
