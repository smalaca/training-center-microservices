package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

@DomainEvent
public record OfferAcceptedEvent(EventId eventId, UUID offerId, UUID trainingId, UUID participantId, Price price) implements OfferEvent {
    public static OfferAcceptedEvent create(UUID offerId, UUID trainingId, UUID participantId, Price price) {
        return new OfferAcceptedEvent(EventId.newEventId(), offerId, trainingId, participantId, price);
    }
}
