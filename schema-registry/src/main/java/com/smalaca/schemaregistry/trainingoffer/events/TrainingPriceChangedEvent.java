package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.math.BigDecimal;
import java.util.UUID;

public record TrainingPriceChangedEvent(EventId eventId, UUID offerId, UUID trainingOfferId, BigDecimal priceAmount, String priceCurrencyCode) {
}