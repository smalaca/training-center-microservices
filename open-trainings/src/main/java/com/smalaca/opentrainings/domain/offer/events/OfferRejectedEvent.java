package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;

@DomainEvent
public record OfferRejectedEvent(EventId eventId, UUID offerId, String reason) implements OfferEvent {
    public static OfferRejectedEvent expired(UUID offerId) {
        return new OfferRejectedEvent(newEventId(), offerId, "Offer expired");
    }

    public static OfferEvent trainingNoLongerAvailable(UUID offerId) {
        return new OfferRejectedEvent(newEventId(), offerId, "Training no longer available");
    }
}
