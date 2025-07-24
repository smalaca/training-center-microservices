package com.smalaca.trainingoffer.domain.trainingoffer.commands;

import com.smalaca.trainingoffer.domain.commandid.CommandId;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmTrainingPriceCommand(CommandId commandId, UUID offerId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode) {
}