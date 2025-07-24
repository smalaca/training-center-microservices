package com.smalaca.trainingscatalogue.trainingoffer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingOfferAssertion {
    private final TrainingOffer actual;

    private TrainingOfferAssertion(TrainingOffer actual) {
        this.actual = actual;
    }

    static TrainingOfferAssertion assertThatTrainingOffer(TrainingOffer actual) {
        return new TrainingOfferAssertion(actual);
    }

    TrainingOfferAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.getTrainingOfferId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasTrainingOfferDraftId(UUID expected) {
        assertThat(actual.getTrainingOfferDraftId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasTrainerId(UUID expected) {
        assertThat(actual.getTrainerId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.getPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasPriceCurrency(String expected) {
        assertThat(actual.getPriceCurrency()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasMinimumParticipants(int expected) {
        assertThat(actual.getMinimumParticipants()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasMaximumParticipants(int expected) {
        assertThat(actual.getMaximumParticipants()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.getStartDate()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.getEndDate()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.getStartTime()).isEqualTo(expected);
        return this;
    }

    TrainingOfferAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.getEndTime()).isEqualTo(expected);
        return this;
    }
}