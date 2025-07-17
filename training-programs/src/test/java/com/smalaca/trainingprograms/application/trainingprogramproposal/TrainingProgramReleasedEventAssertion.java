package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramReleasedEvent;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramReleasedEventAssertion {
    private final TrainingProgramReleasedEvent actual;

    private TrainingProgramReleasedEventAssertion(TrainingProgramReleasedEvent actual) {
        this.actual = actual;
    }

    public static TrainingProgramReleasedEventAssertion assertThatTrainingProgramReleasedEvent(TrainingProgramReleasedEvent actual) {
        return new TrainingProgramReleasedEventAssertion(actual);
    }

    public TrainingProgramReleasedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasTrainingProgramIdNotNull() {
        assertThat(actual.trainingProgramId()).isNotNull();
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasName(String expected) {
        assertThat(actual.name()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasDescription(String expected) {
        assertThat(actual.description()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasAgenda(String expected) {
        assertThat(actual.agenda()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasPlan(String expected) {
        assertThat(actual.plan()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasAuthorId(UUID expected) {
        assertThat(actual.authorId()).isEqualTo(expected);
        return this;
    }
    
    public TrainingProgramReleasedEventAssertion hasReviewerId(UUID reviewerId) {
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
        return this;
    }

    public TrainingProgramReleasedEventAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.categoriesIds()).isEqualTo(expected);
        return this;
    }
}