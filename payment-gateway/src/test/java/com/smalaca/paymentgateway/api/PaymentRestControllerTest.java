package com.smalaca.paymentgateway.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRestControllerTest {
    private final PaymentRestController controller = new PaymentRestController();

    @Test
    void shouldReturnSuccessfulPaymentResponse() {
        PaymentRequest paymentRequest = dummyPaymentRequest();

        PaymentResponse paymentResponse = controller.pay(paymentRequest);

        assertThat(paymentResponse.isSuccessful()).isTrue();
    }

    private PaymentRequest dummyPaymentRequest() {
        return new PaymentRequest(null, null, null, null, null);
    }
}