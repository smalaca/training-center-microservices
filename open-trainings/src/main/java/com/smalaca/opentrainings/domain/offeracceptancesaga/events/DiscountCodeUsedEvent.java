package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@DomainEvent
public record DiscountCodeUsedEvent(
        EventId eventId, UUID offerId, UUID participantId, UUID trainingId, String discountCode,
        BigDecimal originalPrice, BigDecimal newPrice, String priceCurrency) implements OfferAcceptanceSagaEvent {
    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}