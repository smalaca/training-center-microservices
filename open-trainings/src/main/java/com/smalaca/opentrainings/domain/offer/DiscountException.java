package com.smalaca.opentrainings.domain.offer;

public class DiscountException extends RuntimeException {
    DiscountException(String reason) {
        super("Discount Code could not be used because: " + reason);
    }
}
