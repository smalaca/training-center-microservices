package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.math.BigDecimal;
import java.util.UUID;

public record TrainingPriceChangedEvent(EventId eventId, UUID offerId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode) {
}