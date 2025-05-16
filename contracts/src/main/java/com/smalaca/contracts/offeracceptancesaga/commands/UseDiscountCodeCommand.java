package com.smalaca.contracts.offeracceptancesaga.commands;

import com.smalaca.contracts.metadata.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

public record UseDiscountCodeCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) {
}