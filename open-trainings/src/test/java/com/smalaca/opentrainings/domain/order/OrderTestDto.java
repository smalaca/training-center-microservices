package com.smalaca.opentrainings.domain.order;

import com.smalaca.opentrainings.domain.price.Price;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class OrderTestDto {
    private final UUID orderId;
    private final UUID offerId;
    private final UUID trainingId;
    private final UUID participantId;
    private final Price trainingPrice;
    private final Price finalPrice;
    private final String discountCode;
    private final LocalDateTime creationDateTime;
}
