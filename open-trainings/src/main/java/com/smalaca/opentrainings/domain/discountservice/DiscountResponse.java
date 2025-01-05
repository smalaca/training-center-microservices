package com.smalaca.opentrainings.domain.discountservice;

import com.smalaca.opentrainings.domain.price.Price;

public record DiscountResponse(
        boolean isSuccessful, Price newPrice, String failureReason) {
    public static DiscountResponse failed(String failureReason) {
        return new DiscountResponse(false, null, failureReason);
    }

    public static DiscountResponse successful(Price newPrice) {
        return new DiscountResponse(true, newPrice, null);
    }

    public boolean isFailed() {
        return !isSuccessful;
    }
}
