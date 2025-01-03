package com.smalaca.opentrainings.domain.paymentmethod;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.domaindrivendesign.ValueObject;

@ValueObject
public enum PaymentMethod {
    CREDIT_CARD, PAYPAL, BANK_TRANSFER;

    @Factory
    public static PaymentMethod of(String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedPaymentMethodException(paymentMethod);
        }
    }
}
