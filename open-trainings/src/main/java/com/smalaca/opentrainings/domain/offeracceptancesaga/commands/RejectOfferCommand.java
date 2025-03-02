package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;

import java.util.UUID;

public record RejectOfferCommand(CommandId commandId, UUID offerId, String reason) implements OfferAcceptanceSagaCommand {
    public static RejectOfferCommand nextAfter(ExpiredOfferAcceptanceRequestedEvent event, String reason) {
        return new RejectOfferCommand(event.eventId().nextCommandId(), event.offerId(), reason);
    }

    public static RejectOfferCommand nextAfter(TrainingPriceChangedEvent event) {
        return new RejectOfferCommand(event.eventId().nextCommandId(), event.offerId(), "Training price changed to: " + event.priceAmount() + " " + event.priceCurrencyCode());
    }
}
