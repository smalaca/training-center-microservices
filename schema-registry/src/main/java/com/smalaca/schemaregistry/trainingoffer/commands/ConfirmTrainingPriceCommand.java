package com.smalaca.schemaregistry.trainingoffer.commands;

import com.smalaca.schemaregistry.metadata.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmTrainingPriceCommand(CommandId commandId, UUID offerId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode) {
}