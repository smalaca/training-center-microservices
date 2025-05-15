package com.smalaca.contracts.offeracceptancesaga.commands;

import com.smalaca.contracts.metadata.CommandId;
import com.smalaca.contracts.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.math.BigDecimal;
import java.util.UUID;

public record UseDiscountCodeCommand(
        CommandId commandId, UUID offerId, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) implements OfferAcceptanceSagaCommand {
    public static UseDiscountCodeCommand nextAfter(OfferAcceptanceSagaEvent event, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) {
        return new UseDiscountCodeCommand(
                CommandId.nextAfter(event.eventId()), event.offerId(), participantId, trainingId, priceAmount, priceCurrencyCode, discountCode);
    }
}