package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalTrainingProgramReleasedEventAssertion {
    private final TrainingProgramReleasedEvent actual;

    private ExternalTrainingProgramReleasedEventAssertion(TrainingProgramReleasedEvent actual) {
        this.actual = actual;
    }

    static ExternalTrainingProgramReleasedEventAssertion assertThatExternalTrainingProgramReleasedEvent(TrainingProgramReleasedEvent actual) {
        return new ExternalTrainingProgramReleasedEventAssertion(actual);
    }

    ExternalTrainingProgramReleasedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasName(String expected) {
        assertThat(actual.name()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasDescription(String expected) {
        assertThat(actual.description()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasAgenda(String expected) {
        assertThat(actual.agenda()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasPlan(String expected) {
        assertThat(actual.plan()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasAuthorId(UUID expected) {
        assertThat(actual.authorId()).isEqualTo(expected);
        return this;
    }
    
    ExternalTrainingProgramReleasedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasCategoriesIds(List<UUID> expected) {
        assertThat(actual.categoriesIds()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramReleasedEventAssertion hasEventIdWithSameDataAs(EventId expected) {
        assertThat(actual.eventId().eventId()).isEqualTo(expected.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expected.creationDateTime());
        return this;
    }
}