package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramRejectedEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalTrainingProgramRejectedEventAssertion {
    private final TrainingProgramRejectedEvent actual;

    private ExternalTrainingProgramRejectedEventAssertion(TrainingProgramRejectedEvent actual) {
        this.actual = actual;
    }

    static ExternalTrainingProgramRejectedEventAssertion assertThatExternalTrainingProgramRejectedEvent(TrainingProgramRejectedEvent actual) {
        return new ExternalTrainingProgramRejectedEventAssertion(actual);
    }

    ExternalTrainingProgramRejectedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramRejectedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramRejectedEventAssertion hasEventIdWithSameDataAs(EventId expected) {
        assertThat(actual.eventId().eventId()).isEqualTo(expected.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expected.creationDateTime());
        return this;
    }
}