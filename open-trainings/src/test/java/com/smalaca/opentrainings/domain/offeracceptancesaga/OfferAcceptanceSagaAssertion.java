package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

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

    public OfferAcceptanceSagaAssertion isNotCompleted() {
        assertThat(actual.isCompleted()).isFalse();
        return this;
    }

    private OfferAcceptanceSagaAssertion hasStatus(OfferAcceptanceSagaStatus status) {
        assertThat(actual).extracting("status").isEqualTo(status);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasOfferId(UUID expected) {
        assertThat(actual).extracting("offerId").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasDiscountCode(String expected) {
        assertThat(actual).extracting("discountCode").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoDiscountCode() {
        assertThat(actual).extracting("discountCode").isNull();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasDiscountCodeNotUsed() {
        return hasDiscountCodeUsedEqualTo(false);
    }

    public OfferAcceptanceSagaAssertion hasDiscountCodeUsed() {
        return hasDiscountCodeUsedEqualTo(true);
    }

    private OfferAcceptanceSagaAssertion hasDiscountCodeUsedEqualTo(boolean isDiscountCodeUsed) {
        assertThat(actual).extracting("isDiscountCodeUsed").isEqualTo(isDiscountCodeUsed);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasDiscountCodeNotAlreadyUsed() {
        return hasDiscountCodeAlreadyUsedEqualTo(false);
    }

    public OfferAcceptanceSagaAssertion hasDiscountCodeAlreadyUsed() {
        return hasDiscountCodeAlreadyUsedEqualTo(true);
    }

    private OfferAcceptanceSagaAssertion hasDiscountCodeAlreadyUsedEqualTo(boolean isDiscountCodeAlreadyUsed) {
        assertThat(actual).extracting("isDiscountAlreadyCodeUsed").isEqualTo(isDiscountCodeAlreadyUsed);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoParticipantId() {
        assertThat(actual).extracting("participantId").isNull();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasParticipantId(UUID expected) {
        assertThat(actual).extracting("participantId").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasOfferPriceNotConfirmed() {
        return isOfferPriceConfirmedEqualsTo(false);
    }

    public OfferAcceptanceSagaAssertion hasOfferPriceConfirmed() {
        return isOfferPriceConfirmedEqualsTo(true);
    }

    private OfferAcceptanceSagaAssertion isOfferPriceConfirmedEqualsTo(boolean expected) {
        assertThat(actual).extracting("isOfferPriceConfirmed").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoRejectionReason() {
        assertThat(actual).extracting("rejectionReason").isNull();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasRejectionReason(String expected) {
        assertThat(actual).extracting("rejectionReason").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasTrainingId(UUID expected) {
        assertThat(actual).extracting("trainingId").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoTrainingId() {
        assertThat(actual).extracting("trainingId").isNull();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasTrainingPrice(Price expected) {
        assertThat(actual).extracting("trainingPrice").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoTrainingPrice() {
        assertThat(actual).extracting("trainingPrice").isNull();
        return this;
    }

    public OfferAcceptanceSagaAssertion hasNoTrainingPlaceBooked() {
        return hasTrainingPlaceBookedEqualTo(false);
    }

    public OfferAcceptanceSagaAssertion hasTrainingPlaceBooked() {
        return hasTrainingPlaceBookedEqualTo(true);
    }

    private OfferAcceptanceSagaAssertion hasTrainingPlaceBookedEqualTo(boolean expected) {
        assertThat(actual).extracting("isTrainingPlaceBooked").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion hasAvailableTrainingPlacesLeft() {
        return hasNoAvailableTrainingPlacesLeftEqualTo(false);
    }

    public OfferAcceptanceSagaAssertion hasNoAvailableTrainingPlacesLeft() {
        return hasNoAvailableTrainingPlacesLeftEqualTo(true);
    }

    private OfferAcceptanceSagaAssertion hasNoAvailableTrainingPlacesLeftEqualTo(boolean expected) {
        assertThat(actual).extracting("hasNoAvailableTrainingPlacesLeft").isEqualTo(expected);
        return this;
    }

    public OfferAcceptanceSagaAssertion consumedNoEvents() {
        return consumedEvents(0);
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
