package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalReleaseFailedEvent;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramProposalReleaseFailedEventAssertion {
    private final TrainingProgramProposalReleaseFailedEvent actual;

    private TrainingProgramProposalReleaseFailedEventAssertion(TrainingProgramProposalReleaseFailedEvent actual) {
        this.actual = actual;
    }

    public static TrainingProgramProposalReleaseFailedEventAssertion assertThatTrainingProgramProposalReleaseFailedEvent(TrainingProgramProposalReleaseFailedEvent actual) {
        return new TrainingProgramProposalReleaseFailedEventAssertion(actual);
    }

    public TrainingProgramProposalReleaseFailedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalReleaseFailedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }
}