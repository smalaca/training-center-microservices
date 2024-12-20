package com.smalaca.opentrainings.infrastructure.clock.localdatetime;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeClockTest {

    @Test
    void shouldReturnLocalDateTimeNow() {
        LocalDateTimeClock clock = new LocalDateTimeClock();
        LocalDateTime before = LocalDateTime.now();

        LocalDateTime actual = clock.now();

        assertThat(actual)
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(LocalDateTime.now());
    }
}