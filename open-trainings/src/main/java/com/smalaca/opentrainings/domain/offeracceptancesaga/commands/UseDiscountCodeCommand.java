package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

public record UseDiscountCodeCommand(
        CommandId commandId, UUID offerId, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) implements OfferAcceptanceSagaCommand {
}