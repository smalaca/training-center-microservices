package com.smalaca.opentrainings.domain.discountservice;

import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

public record DiscountCodeDto(UUID participantId, UUID trainingId, Price price, String discountCode) {
}
