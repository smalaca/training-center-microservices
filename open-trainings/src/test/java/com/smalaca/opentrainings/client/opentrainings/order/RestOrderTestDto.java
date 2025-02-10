package com.smalaca.opentrainings.client.opentrainings.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RestOrderTestDto(
        UUID orderId, UUID offerId, UUID trainingId, UUID participantId, String orderNumber,
        BigDecimal trainingPriceAmount, String trainingPriceCurrency, BigDecimal finalPriceAmount, String finalPriceCurrency,
        LocalDateTime creationDateTime, String status, String discountCode) {
}
