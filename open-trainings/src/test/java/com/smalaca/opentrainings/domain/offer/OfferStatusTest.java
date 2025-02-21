package com.smalaca.opentrainings.domain.offer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTANCE_IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.INITIATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class OfferStatusTest {

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, mode = EXCLUDE, names = "INITIATED")
    void shouldRecognizeNotInitiated(OfferStatus actual) {
        assertThat(actual.isNotInitiated()).isTrue();
    }

    @Test
    void shouldRecognizeInitiated() {
        assertThat(INITIATED.isNotInitiated()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = OfferStatus.class, mode = EXCLUDE, names = "ACCEPTANCE_IN_PROGRESS")
    void shouldRecognizeAcceptanceNotInProgress(OfferStatus actual) {
        assertThat(actual.isAcceptanceNotInProgress()).isTrue();
    }

    @Test
    void shouldRecognizeAcceptanceInProgress() {
        assertThat(ACCEPTANCE_IN_PROGRESS.isAcceptanceNotInProgress()).isFalse();
    }
}