package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;

import java.math.BigDecimal;
import java.util.UUID;

public record DiscountCodeUsedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId, String discountCode, BigDecimal originalPrice, BigDecimal newPrice, String priceCurrency) {
}