package com.smalaca.trainingscatalogue.trainingoffer;

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

    public TrainingOfferAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.getTrainingOfferId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.getTrainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasTrainerId(UUID expected) {
        assertThat(actual.getTrainerId()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.getPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasPriceCurrency(String expected) {
        assertThat(actual.getPriceCurrency()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasAvailablePlaces(int expected) {
        assertThat(actual.getAvailablePlaces()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.getStartDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.getEndDate()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.getStartTime()).isEqualTo(expected);
        return this;
    }

    public TrainingOfferAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.getEndTime()).isEqualTo(expected);
        return this;
    }
}