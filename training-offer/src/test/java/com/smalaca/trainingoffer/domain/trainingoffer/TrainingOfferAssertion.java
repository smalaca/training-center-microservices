package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingOfferAssertion {
    private final TrainingOffer actual;

    private TrainingOfferAssertion(TrainingOffer actual) {
        this.actual = actual;
    }

    public static TrainingOfferAssertion assertThatTrainingOffer(TrainingOffer actual) {
        return new TrainingOfferAssertion(actual);
    }

    public TrainingOfferAssertion hasTrainingOfferIdNotNull() {
        assertThat(actual).extracting("trainingOfferId").isNotNull();
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
        assertThat(actual).hasFieldOrPropertyWithValue("minimumParticipants", expected);
        return this;
    }

    public TrainingOfferAssertion hasMaximumParticipants(int expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("maximumParticipants", expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainingSessionPeriod(
            LocalDate expectedStartDate, LocalDate expectedEndDate, LocalTime expectedStartTime, LocalTime expectedEndTime) {
        TrainingSessionPeriod expected = new TrainingSessionPeriod(expectedStartDate, expectedEndDate, expectedStartTime, expectedEndTime);
        assertThat(actual).hasFieldOrPropertyWithValue("trainingSessionPeriod", expected);

        return this;
    }
}