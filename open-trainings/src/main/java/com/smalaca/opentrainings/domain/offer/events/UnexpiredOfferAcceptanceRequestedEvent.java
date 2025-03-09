package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UnexpiredOfferAcceptanceRequestedEvent(EventId eventId, UUID offerId, UUID trainingId, BigDecimal trainingPriceAmount, String trainingPriceCurrencyCode) implements OfferEvent, OfferAcceptanceSagaEvent {
    public static UnexpiredOfferAcceptanceRequestedEvent nextAfter(BeginOfferAcceptanceCommand command, UUID trainingId, Price trainingPrice) {
        return new UnexpiredOfferAcceptanceRequestedEvent(command.commandId().nextEventId(), command.offerId(), trainingId, trainingPrice.amount(), trainingPrice.currencyCode());
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}
