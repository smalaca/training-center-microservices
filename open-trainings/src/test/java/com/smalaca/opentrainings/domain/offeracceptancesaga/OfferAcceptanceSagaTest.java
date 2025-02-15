package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion.assertThatOfferAcceptanceSaga;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OfferAcceptanceSagaTest {
    private static final Faker FAKER = new Faker();
    private static final UUID OFFER_ID = randomId();
    private static final LocalDateTime NOW = LocalDateTime.now();

    private final Clock clock = givenClock();

    private Clock givenClock() {
        Clock clock = mock(Clock.class);
        given(clock.now()).willReturn(NOW);

        return clock;
    }

    @Test
    void shouldRecognizeCreatedOfferAcceptanceSagaAsNotCompleted() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);

        assertThatOfferAcceptanceSaga(actual).isInProgress();
    }

    @Test
    void shouldRecognizeOfferAcceptanceRequestedAsCompleted() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        actual.accept(event, clock);

        assertThatOfferAcceptanceSaga(actual)
                .isCompleted()
                .hasOfferId(OFFER_ID)
                .consumedEvents(1)
                .consumedEventAt(event, NOW);
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().imei());
    }
}