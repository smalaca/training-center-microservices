package com.smalaca.paymentgateway.api;

public record PaymentResponse(boolean isSuccessful) {
    public static PaymentResponse successful() {
        return new PaymentResponse(true);
    }
}
