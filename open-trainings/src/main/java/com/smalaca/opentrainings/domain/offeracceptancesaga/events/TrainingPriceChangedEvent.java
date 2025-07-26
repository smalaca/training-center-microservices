package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingPriceChangedEvent(EventId eventId, UUID offerId, UUID trainingOfferId, BigDecimal priceAmount, String priceCurrencyCode) implements OfferAcceptanceSagaEvent {
    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}