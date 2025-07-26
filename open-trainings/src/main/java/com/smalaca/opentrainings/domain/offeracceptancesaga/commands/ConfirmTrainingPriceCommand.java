package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmTrainingPriceCommand(CommandId commandId, UUID offerId, UUID trainingOfferId, BigDecimal priceAmount, String priceCurrencyCode) implements OfferAcceptanceSagaCommand {
    public static ConfirmTrainingPriceCommand nextAfter(ExpiredOfferAcceptanceRequestedEvent event) {
        return new ConfirmTrainingPriceCommand(
                CommandId.nextAfter(event.eventId()), event.offerId(), event.trainingId(),
                event.trainingPriceAmount(), event.trainingPriceCurrencyCode());
    }
}
