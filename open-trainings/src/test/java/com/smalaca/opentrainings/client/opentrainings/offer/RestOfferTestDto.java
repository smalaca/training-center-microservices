package com.smalaca.opentrainings.client.opentrainings.offer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RestOfferTestDto(
        UUID offerId, UUID trainingId, String offerNumber, BigDecimal trainingPriceAmount, String trainingPriceCurrency,
        LocalDateTime creationDateTime, String status) {
}
