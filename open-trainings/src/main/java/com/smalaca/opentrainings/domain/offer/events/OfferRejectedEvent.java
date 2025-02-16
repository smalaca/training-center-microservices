package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;

@DomainEvent
public record OfferRejectedEvent(EventId eventId, UUID offerId, String reason) implements OfferEvent {
    public static OfferRejectedEvent create(UUID offerId, String reason) {
        return new OfferRejectedEvent(newEventId(), offerId, reason);
    }

    public static OfferRejectedEvent nextAfter(RejectOfferCommand command) {
        return new OfferRejectedEvent(command.commandId().nextEventId(), command.offerId(), command.reason());
    }
}
