package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;

public class OfferAcceptanceSagaAssertion {
    private final OfferAcceptanceSaga actual;

    private OfferAcceptanceSagaAssertion(OfferAcceptanceSaga actual) {
        this.actual = actual;
    }

    public static OfferAcceptanceSagaAssertion assertThatOfferAcceptanceSaga(OfferAcceptanceSaga actual) {
        return new OfferAcceptanceSagaAssertion(actual);
    }

    public OfferAcceptanceSagaAssertion isInProgress() {
        return hasStatus(IN_PROGRESS);
    }

    public OfferAcceptanceSagaAssertion isAccepted() {
        return hasStatus(ACCEPTED);
    }

    public OfferAcceptanceSagaAssertion isRejected() {
        return hasStatus(REJECTED);
    }

    private OfferAcceptanceSagaAssertion hasStatus(OfferAcceptanceSagaStatus status) {
        assertThat(actual.getStatus()).isEqualTo(status);
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

    public OfferAcceptanceSagaAssertion consumedEventAt(OfferAcceptanceSagaEvent expectedEvent, LocalDateTime expectedConsumedAt) {
        assertThat(actual).extracting("events").satisfies(events -> {
            assertThat((List<ConsumedEvent>) events).anySatisfy(acceptedEvent -> {
                ConsumedEvent expected = new ConsumedEvent(expectedEvent.eventId(), expectedConsumedAt, expectedEvent);
                assertThat(acceptedEvent).isEqualTo(expected);
            });
        });

        return this;
    }
}
