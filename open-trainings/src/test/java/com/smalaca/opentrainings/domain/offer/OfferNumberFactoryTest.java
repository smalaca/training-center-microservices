package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.data.Random;
import com.smalaca.opentrainings.domain.clock.Clock;
import net.datafaker.Faker;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OfferNumberFactoryTest {
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final Number RANDOM_NUMBER = new Faker().number();
    private static final int YEAR = RANDOM_NUMBER.numberBetween(2000, 2100);
    private static final UUID PARTICIPANT_ID = Random.randomId();

    private final Clock clock = mock(Clock.class);
    private final OfferNumberFactory factory = new OfferNumberFactory(clock);

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void shouldCreateOfferNumberForMonthsWithOneDigit(int month) {
        givenNowFor(month);

        OfferNumber actual = factory.createFor(PARTICIPANT_ID);

        assertThat(actual.value()).matches("OFR/" + YEAR + "/0" + month + "/" + PARTICIPANT_ID + "/" + UUID_REGEX);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12})
    void shouldCreateOfferNumberForMonthsWithTwoDigit(int month) {
        givenNowFor(month);

        OfferNumber actual = factory.createFor(PARTICIPANT_ID);

        assertThat(actual.value()).matches("OFR/" + YEAR + "/" + month + "/" + PARTICIPANT_ID + "/" + UUID_REGEX);
    }

    private void givenNowFor(int month) {
        LocalDate date = LocalDate.of(YEAR, month, RANDOM_NUMBER.numberBetween(1, 20));
        given(clock.now()).willReturn(LocalDateTime.of(date, LocalTime.now()));
    }
}