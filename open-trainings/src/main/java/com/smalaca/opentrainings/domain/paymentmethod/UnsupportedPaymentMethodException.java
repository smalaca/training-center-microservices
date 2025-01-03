package com.smalaca.opentrainings.domain.paymentmethod;

public class UnsupportedPaymentMethodException extends RuntimeException {
    UnsupportedPaymentMethodException(String paymentMethod) {
        super("Unsupported payment method: " + paymentMethod);
    }
}
