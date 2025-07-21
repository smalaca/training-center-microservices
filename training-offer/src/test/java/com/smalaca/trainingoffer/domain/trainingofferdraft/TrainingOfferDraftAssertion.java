package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferDraftAssertion {
    private final TrainingOfferDraft actual;

    private TrainingOfferDraftAssertion(TrainingOfferDraft actual) {
        this.actual = actual;
    }

    public static TrainingOfferDraftAssertion assertThatTrainingOfferDraft(TrainingOfferDraft actual) {
        return new TrainingOfferDraftAssertion(actual);
    }

    public TrainingOfferDraftAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingOfferDraftId", expected);
        return this;
    }

    public TrainingOfferDraftAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingProgramId", expected);
        return this;
    }

    public TrainingOfferDraftAssertion hasTrainerId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainerId", expected);
        return this;
    }

    public TrainingOfferDraftAssertion hasPrice(BigDecimal expectedPriceAmount, String expectedPriceCurrency) {
        Price expected = Price.of(expectedPriceAmount, expectedPriceCurrency);
        assertThat(actual).hasFieldOrPropertyWithValue("price", expected);

        return this;
    }

    public TrainingOfferDraftAssertion hasMinimumParticipants(int expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("minimumParticipants", expected);
        return this;
    }

    public TrainingOfferDraftAssertion hasMaximumParticipants(int expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("maximumParticipants", expected);
        return this;
    }

    public TrainingOfferDraftAssertion hasTrainingSessionPeriod(
            LocalDate expectedStartDate, LocalDate expectedEndDate, LocalTime expectedStartTime, LocalTime expectedEndTime) {
        TrainingSessionPeriod expected = new TrainingSessionPeriod(expectedStartDate, expectedEndDate, expectedStartTime, expectedEndTime);
        assertThat(actual).hasFieldOrPropertyWithValue("trainingSessionPeriod", expected);

        return this;
    }

    public TrainingOfferDraftAssertion isPublished() {
        assertThat(actual).hasFieldOrPropertyWithValue("published", true);
        return this;
    }

    public TrainingOfferDraftAssertion isNotPublished() {
        assertThat(actual).hasFieldOrPropertyWithValue("published", false);
        return this;
    }
}