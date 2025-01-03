package com.smalaca.opentrainings.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class OrderTestDto {
    private final UUID orderId;
    private final UUID offerId;
    private final UUID trainingId;
    private final UUID participantId;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDateTime creationDateTime;
}
