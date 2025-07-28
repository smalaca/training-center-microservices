package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferStatus.PUBLISHED;
import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferStatus.RESCHEDULED;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferAssertion {
    private final TrainingOffer actual;

    private TrainingOfferAssertion(TrainingOffer actual) {
        this.actual = actual;
    }

    public static TrainingOfferAssertion assertThatTrainingOffer(TrainingOffer actual) {
        return new TrainingOfferAssertion(actual);
    }

    public TrainingOfferAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual).extracting("trainingOfferId").isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingOfferDraftId", expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingProgramId", expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainerId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainerId", expected);
        return this;
    }

    public TrainingOfferAssertion hasPrice(BigDecimal expectedPriceAmount, String expectedPriceCurrency) {
        Price expected = Price.of(expectedPriceAmount, expectedPriceCurrency);
        assertThat(actual).hasFieldOrPropertyWithValue("price", expected);

        return this;
    }

    public TrainingOfferAssertion hasMinimumParticipants(int expected) {
        assertThat(actual).extracting("participants").hasFieldOrPropertyWithValue("minimumParticipants", expected);
        return this;
    }

    public TrainingOfferAssertion hasMaximumParticipants(int expected) {
        assertThat(actual).extracting("participants").hasFieldOrPropertyWithValue("maximumParticipants", expected);
        return this;
    }

    public TrainingOfferAssertion hasNoParticipantsRegistered() {
        return hasParticipants(actualParticipants -> assertThat(actualParticipants).isEmpty());
    }

    public TrainingOfferAssertion hasParticipantsRegistered(int expected) {
        return hasParticipants(actualParticipants -> assertThat(actualParticipants).hasSize(expected));
    }

    public TrainingOfferAssertion hasRegisteredParticipant(UUID expected) {
        return hasParticipants(actualParticipants -> assertThat(actualParticipants).contains(expected));
    }

    public TrainingOfferAssertion hasNoRegisteredParticipant(UUID expected) {
        return hasParticipants(actualParticipants -> {
            assertThat(actualParticipants).isNotEmpty().doesNotContain(expected);
        });
    }

    private TrainingOfferAssertion hasParticipants(Consumer<Set<UUID>> assertion) {
        assertThat(actual).extracting("participants").extracting("participantIds").satisfies(participantIds -> {
            assertion.accept((Set<UUID>) participantIds);
        });
        return this;
    }

    public TrainingOfferAssertion hasTrainingSessionPeriod(
            LocalDate expectedStartDate, LocalDate expectedEndDate, LocalTime expectedStartTime, LocalTime expectedEndTime) {
        TrainingSessionPeriod expected = new TrainingSessionPeriod(expectedStartDate, expectedEndDate, expectedStartTime, expectedEndTime);
        assertThat(actual).hasFieldOrPropertyWithValue("trainingSessionPeriod", expected);

        return this;
    }

    public TrainingOfferAssertion  isPublished() {
        return hasStatus(PUBLISHED);
    }

    public TrainingOfferAssertion isRescheduled() {
        return hasStatus(RESCHEDULED);
    }

    private TrainingOfferAssertion hasStatus(TrainingOfferStatus expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("status", expected);
        return this;
    }
}