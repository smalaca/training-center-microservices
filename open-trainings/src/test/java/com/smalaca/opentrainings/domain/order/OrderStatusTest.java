package com.smalaca.opentrainings.domain.order;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"INITIATED"})
    void shouldRecognizeFinalStatus(OrderStatus actual) {
        assertThat(actual.isFinal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"INITIATED"})
    void shouldRecognizeNonFinalStatus(OrderStatus actual) {
        assertThat(actual.isFinal()).isFalse();
    }
}