package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramProposalCreatedEventAssertion {
    private final TrainingProgramProposedEvent actual;

    private TrainingProgramProposalCreatedEventAssertion(TrainingProgramProposedEvent actual) {
        this.actual = actual;
    }

    public static TrainingProgramProposalCreatedEventAssertion assertThatTrainingProgramProposalCreatedEvent(TrainingProgramProposedEvent actual) {
        return new TrainingProgramProposalCreatedEventAssertion(actual);
    }

    public TrainingProgramProposalCreatedEventAssertion hasTrainingProgramProposalIdNotNull() {
        assertThat(actual.trainingProgramProposalId()).isNotNull();
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasName(String expected) {
        assertThat(actual.name()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasDescription(String expected) {
        assertThat(actual.description()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasAgenda(String expected) {
        assertThat(actual.agenda()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasPlan(String expected) {
        assertThat(actual.plan()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasAuthorId(UUID expected) {
        assertThat(actual.authorId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposalCreatedEventAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.categoriesIds()).isEqualTo(expected);
        return this;
    }
}