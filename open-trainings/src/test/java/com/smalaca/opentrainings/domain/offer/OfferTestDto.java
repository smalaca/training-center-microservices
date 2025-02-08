package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.price.Price;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class OfferTestDto {
    private final UUID offerId;
    private final UUID trainingId;
    private final Price trainingPrice;
    private final LocalDateTime creationDateTime;
}
