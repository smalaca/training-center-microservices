package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferAcceptanceSagaAssertion {
    private final OfferAcceptanceSaga actual;

    private OfferAcceptanceSagaAssertion(OfferAcceptanceSaga actual) {
        this.actual = actual;
    }

    public static OfferAcceptanceSagaAssertion assertThatOfferAcceptanceSaga(OfferAcceptanceSaga actual) {
        return new OfferAcceptanceSagaAssertion(actual);
    }

    public OfferAcceptanceSagaAssertion isCompleted() {
        assertThat(actual.isCompleted()).isTrue();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasOfferId(UUID expected) {
        assertThat(actual).extracting("offerId").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion consumedEvents(int expected) {
        assertThat(actual).extracting("events").satisfies(events -> {
            assertThat((List<ConsumedEvent>) events).hasSize(expected);
        });

        return this;
    }

    public OfferAcceptanceSagaAssertion consumedEventAt(OfferAcceptanceRequestedEvent expectedEvent, LocalDateTime expectedConsumedAt) {
        assertThat(actual).extracting("events").satisfies(events -> {
            assertThat((List<ConsumedEvent>) events).anySatisfy(acceptedEvent -> {
                ConsumedEvent expected = new ConsumedEvent(expectedEvent.eventId(), expectedConsumedAt, expectedEvent);
                assertThat(acceptedEvent).isEqualTo(expected);
            });
        });

        return this;
    }
}
