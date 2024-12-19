package com.smalaca.opentrainings.domain.paymentgateway;

public record PaymentResponse(boolean isSuccessful) {
    public static PaymentResponse successful() {
        return new PaymentResponse(true);
    }

    public static PaymentResponse failed() {
        return new PaymentResponse(false);
    }
}
