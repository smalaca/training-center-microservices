package com.smalaca.opentrainings.domain.offer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

class OfferStatusTest {

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, mode = INCLUDE, names = {"ACCEPTED", "REJECTED", "DECLINED", "TERMINATED"})
    void shouldRecognizeFinalStatus(OfferStatus actual) {
        assertThat(actual.isFinal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, mode = EXCLUDE, names = {"ACCEPTED", "REJECTED", "DECLINED", "TERMINATED"})
    void shouldRecognizeNonFinalStatus(OfferStatus actual) {
        assertThat(actual.isFinal()).isFalse();
    }
}