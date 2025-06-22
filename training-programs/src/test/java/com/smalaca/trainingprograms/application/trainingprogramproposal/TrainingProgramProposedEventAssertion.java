package com.smalaca.trainingprograms.application.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.commandid.CommandId;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposedEvent;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainingProgramProposedEventAssertion {
    private final TrainingProgramProposedEvent actual;

    private TrainingProgramProposedEventAssertion(TrainingProgramProposedEvent actual) {
        this.actual = actual;
    }

    public static TrainingProgramProposedEventAssertion assertThatTrainingProgramProposedEvent(TrainingProgramProposedEvent actual) {
        return new TrainingProgramProposedEventAssertion(actual);
    }

    public TrainingProgramProposedEventAssertion isNextAfter(CommandId commandId) {
        assertThat(actual.eventId().eventId()).isNotEqualTo(commandId.commandId());
        assertThat(actual.eventId().traceId()).isEqualTo(commandId.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(commandId.correlationId());
        assertThat(actual.eventId().creationDateTime()).isAfterOrEqualTo(commandId.creationDateTime());
        return this;
    }

    public TrainingProgramProposedEventAssertion hasTrainingProgramProposalIdNotNull() {
        assertThat(actual.trainingProgramProposalId()).isNotNull();
        return this;
    }

    public TrainingProgramProposedEventAssertion hasName(String expected) {
        assertThat(actual.name()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposedEventAssertion hasDescription(String expected) {
        assertThat(actual.description()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposedEventAssertion hasAgenda(String expected) {
        assertThat(actual.agenda()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposedEventAssertion hasPlan(String expected) {
        assertThat(actual.plan()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposedEventAssertion hasAuthorId(UUID expected) {
        assertThat(actual.authorId()).isEqualTo(expected);
        return this;
    }

    public TrainingProgramProposedEventAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.categoriesIds()).isEqualTo(expected);
        return this;
    }
}