package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

record DiscountTestDto(UUID trainingId, UUID participantId, String discountCode, Price trainingPrice, Price newPrice) {
    DiscountCodeDto asDiscountCodeDto() {
        return new DiscountCodeDto(participantId(), trainingId(), trainingPrice(), discountCode());
    }

    DiscountResponse asDiscountResponse() {
        return DiscountResponse.successful(newPrice());
    }
}
