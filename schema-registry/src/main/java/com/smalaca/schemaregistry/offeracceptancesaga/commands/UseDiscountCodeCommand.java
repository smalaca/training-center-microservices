package com.smalaca.schemaregistry.offeracceptancesaga.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

public record UseDiscountCodeCommand(CommandId commandId, UUID offerId, UUID participantId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode, String discountCode) {
}