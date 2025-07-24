package com.smalaca.trainingscatalogue.trainingprogram;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramAssertion {
    private final TrainingProgram actual;

    private TrainingProgramAssertion(TrainingProgram actual) {
        this.actual = actual;
    }

    public static TrainingProgramAssertion assertThatTrainingProgram(TrainingProgram actual) {
        return new TrainingProgramAssertion(actual);
    }

    public TrainingProgramAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.getTrainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasAuthorId(UUID expected) {
        assertThat(actual.getAuthorId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasReviewerId(UUID expected) {
        assertThat(actual.getReviewerId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasName(String expected) {
        assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasAgenda(String expected) {
        assertThat(actual.getAgenda()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasPlan(String expected) {
        assertThat(actual.getPlan()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasDescription(String expected) {
        assertThat(actual.getDescription()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.getCategoriesIds()).containsExactlyInAnyOrderElementsOf(expected);
        return this;
    }
}