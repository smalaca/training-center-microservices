package com.smalaca.trainingprograms.domain.trainingprogramproposal;

import java.util.List;
import java.util.UUID;

import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.PROPOSED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.REJECTED;
import static com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalStatus.RELEASED;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramProposalAssertion {
    private final TrainingProgramProposal actual;

    private TrainingProgramProposalAssertion(TrainingProgramProposal actual) {
        this.actual = actual;
    }

    public static TrainingProgramProposalAssertion assertThatTrainingProgramProposal(TrainingProgramProposal actual) {
        return new TrainingProgramProposalAssertion(actual);
    }

    public TrainingProgramProposalAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("trainingProgramProposalId", expected);
        return this;
    }

    public TrainingProgramProposalAssertion hasName(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("name", expected);
        return this;
    }

    public TrainingProgramProposalAssertion hasDescription(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("description", expected);
        return this;
    }

    public TrainingProgramProposalAssertion hasAgenda(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("agenda", expected);
        return this;
    }

    public TrainingProgramProposalAssertion hasPlan(String expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("plan", expected);
        return this;
    }

    public TrainingProgramProposalAssertion hasAuthorId(UUID expected) {
        assertThat(actual).hasFieldOrPropertyWithValue("authorId", expected);
        return this;
    }
    
    public TrainingProgramProposalAssertion hasNullReviewerId() {
        assertThat(actual).hasFieldOrPropertyWithValue("reviewerId", null);
        return this;
    }

    public TrainingProgramProposalAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual)
                .extracting("categoriesIds")
                .satisfies(categoriesIds -> assertThat((List<UUID>) categoriesIds).containsExactlyInAnyOrderElementsOf(expected));

        return this;
    }

    public TrainingProgramProposalAssertion isProposed() {
        return hasStatus(PROPOSED);
    }

    public TrainingProgramProposalAssertion isReleased() {
        return hasStatus(RELEASED);
    }
    
    public TrainingProgramProposalAssertion isRejected() {
        return hasStatus(REJECTED);
    }

    public TrainingProgramProposalAssertion hasStatus(TrainingProgramProposalStatus expected) {
        assertThat(actual).extracting("status").isEqualTo(expected);
        return this;
    }
}
