package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.time.LocalDateTime;
import java.util.UUID;

@DomainEvent
public record OfferAcceptanceRequestedEvent(EventId eventId, UUID offerId, String firstName, String lastName, String email, String discountCode) implements OfferAcceptanceSagaEvent{
    public static OfferAcceptanceRequestedEvent create(UUID sagaId, String firstName, String lastName, String email, String discountCode) {
        return new OfferAcceptanceRequestedEvent(EventId.newEventId(), sagaId, firstName, lastName, email, discountCode);
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}
