package com.smalaca.trainingprograms.domain.trainingprogram;

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

    public TrainingProgramAssertion hasTrainingProgramIdNotNull() {
        assertThat(actual).extracting("trainingProgramId").isNotNull();
        return this;
    }

    public TrainingProgramAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingProgramId", expected);
        return this;
    }

    public TrainingProgramAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingProgramProposalId", expected);
        return this;
    }

    public TrainingProgramAssertion hasName(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("name", expected);
        return this;
    }

    public TrainingProgramAssertion hasDescription(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("description", expected);
        return this;
    }

    public TrainingProgramAssertion hasAgenda(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("agenda", expected);
        return this;
    }

    public TrainingProgramAssertion hasPlan(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("plan", expected);
        return this;
    }

    public TrainingProgramAssertion hasAuthorId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("authorId", expected);
        return this;
    }

    public TrainingProgramAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual)
                .extracting("categoriesIds")
                .satisfies(categoriesIds -> assertThat((List<UUID>) categoriesIds).containsExactlyInAnyOrderElementsOf(expected));

        return this;
    }
}