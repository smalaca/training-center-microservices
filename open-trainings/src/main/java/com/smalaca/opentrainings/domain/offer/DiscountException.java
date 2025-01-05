package com.smalaca.opentrainings.domain.offer;

public class DiscountException extends RuntimeException {
    public DiscountException(String reason) {
        super("Discount Code could not be used because: " + reason);
    }
}
