package com.smalaca.trainingscatalogue.trainingprogram;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramSummaryAssertion {
    private final TrainingProgramSummary actual;

    private TrainingProgramSummaryAssertion(TrainingProgramSummary actual) {
        this.actual = actual;
    }

    public static TrainingProgramSummaryAssertion assertThatTrainingProgramSummary(TrainingProgramSummary actual) {
        return new TrainingProgramSummaryAssertion(actual);
    }

    TrainingProgramSummaryAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    TrainingProgramSummaryAssertion hasAuthorId(UUID expected) {
        assertThat(actual.getAuthorId()).isEqualTo(expected);
        return this;
    }

    TrainingProgramSummaryAssertion hasName(String expected) {
        assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }
}