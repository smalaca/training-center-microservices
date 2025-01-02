package com.smalaca.opentrainings.client.opentrainings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RestOrderTestDto(
        UUID orderId, UUID offerId, UUID trainingId, UUID participantId, BigDecimal priceAmount,
        String priceCurrency, LocalDateTime creationDateTime, String status) {
}
