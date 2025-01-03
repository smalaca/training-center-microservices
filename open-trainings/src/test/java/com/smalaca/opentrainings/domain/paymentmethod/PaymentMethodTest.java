package com.smalaca.opentrainings.domain.paymentmethod;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentMethodTest {

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "VISA", "MASTERCARD"})
    void shouldRecognizeUnsupportedPaymentMethods(String unsupported) {
        UnsupportedPaymentMethodException actual = assertThrows(UnsupportedPaymentMethodException.class, () -> PaymentMethod.of(unsupported));

        assertThat(actual).hasMessage("Unsupported payment method: " + unsupported);
    }

    @ParameterizedTest
    @EnumSource(PaymentMethod.class)
    void shouldRecognizeAllSupportedPaymentMethods(PaymentMethod paymentMethod) {
        PaymentMethod actual = PaymentMethod.of(paymentMethod.name());

        assertThat(actual.name()).isSameAs(paymentMethod.name());
    }
}