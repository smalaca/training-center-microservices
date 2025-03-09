package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.math.BigDecimal;
import java.util.UUID;

public record UseDiscountCodeCommand(
        CommandId commandId, UUID offerId, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) implements OfferAcceptanceSagaCommand {
    public static OfferAcceptanceSagaCommand nextAfter(OfferAcceptanceSagaEvent event, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) {
        return new UseDiscountCodeCommand(
                CommandId.nextAfter(event.eventId()), event.offerId(), participantId, trainingId, priceAmount, priceCurrencyCode, discountCode);
    }
}