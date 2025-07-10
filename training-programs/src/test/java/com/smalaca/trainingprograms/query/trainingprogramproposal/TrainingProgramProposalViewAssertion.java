package com.smalaca.trainingprograms.query.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramProposalViewAssertion {
    private final TrainingProgramProposalView actual;

    private TrainingProgramProposalViewAssertion(TrainingProgramProposalView actual) {
        this.actual = actual;
    }

    static TrainingProgramProposalViewAssertion assertThatTrainingProgramProposal(TrainingProgramProposalView actual) {
        return new TrainingProgramProposalViewAssertion(actual);
    }

    TrainingProgramProposalViewAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.getTrainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasName(String expected) {
        assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasDescription(String expected) {
        assertThat(actual.getDescription()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasAgenda(String expected) {
        assertThat(actual.getAgenda()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasPlan(String expected) {
        assertThat(actual.getPlan()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasAuthorId(UUID expected) {
        assertThat(actual.getAuthorId()).isEqualTo(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.getCategoriesIds()).containsExactlyInAnyOrderElementsOf(expected);
        return this;
    }

    TrainingProgramProposalViewAssertion hasStatus(TrainingProgramProposalStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.name());
        return this;
    }
}
