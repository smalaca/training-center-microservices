package com.smalaca.trainingscatalogue.trainingoffer;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingOfferSummaryAssertion {
    private final TrainingOfferSummary actual;

    private TrainingOfferSummaryAssertion(TrainingOfferSummary actual) {
        this.actual = actual;
    }

    public static TrainingOfferSummaryAssertion assertThatTrainingOfferSummary(TrainingOfferSummary actual) {
        return new TrainingOfferSummaryAssertion(actual);
    }

    TrainingOfferSummaryAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.getTrainingOfferId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferSummaryAssertion hasTrainerId(UUID expected) {
        assertThat(actual.getTrainerId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferSummaryAssertion hasTrainingProgramName(String expected) {
        assertThat(actual.getTrainingProgramName()).isEqualTo(expected);
        return this;
    }

    TrainingOfferSummaryAssertion hasNoTrainingProgramName() {
        assertThat(actual.getTrainingProgramName()).isEqualTo("NO NAME");
        return this;
    }

    TrainingOfferSummaryAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.getStartDate()).isEqualTo(expected);
        return this;
    }

    TrainingOfferSummaryAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.getEndDate()).isEqualTo(expected);
        return this;
    }
}