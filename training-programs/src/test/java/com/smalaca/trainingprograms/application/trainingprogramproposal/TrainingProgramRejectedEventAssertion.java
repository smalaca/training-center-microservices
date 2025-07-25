package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramRejectedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramRejectedEventAssertion {
    private final TrainingProgramRejectedEvent actual;

    private TrainingProgramRejectedEventAssertion(TrainingProgramRejectedEvent actual) {
        this.actual = actual;
    }

    public static TrainingProgramRejectedEventAssertion assertThatTrainingProgramRejectedEvent(TrainingProgramRejectedEvent actual) {
        return new TrainingProgramRejectedEventAssertion(actual);
    }

    public TrainingProgramRejectedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramRejectedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }
}