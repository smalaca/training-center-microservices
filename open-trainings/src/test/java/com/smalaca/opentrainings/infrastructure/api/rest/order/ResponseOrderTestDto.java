package com.smalaca.opentrainings.infrastructure.api.rest.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseOrderTestDto(
        UUID orderId, UUID trainingId, UUID participantId, BigDecimal priceAmount,
        String priceCurrency, LocalDateTime creationDateTime, String status) {
}
