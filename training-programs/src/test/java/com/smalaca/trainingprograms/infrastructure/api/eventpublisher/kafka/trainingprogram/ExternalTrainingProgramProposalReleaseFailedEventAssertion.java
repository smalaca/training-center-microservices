package com.smalaca.trainingprograms.infrastructure.api.eventpublisher.kafka.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramProposalReleaseFailedEvent;
import com.smalaca.trainingprograms.domain.eventid.EventId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalTrainingProgramProposalReleaseFailedEventAssertion {
    private final TrainingProgramProposalReleaseFailedEvent actual;

    private ExternalTrainingProgramProposalReleaseFailedEventAssertion(TrainingProgramProposalReleaseFailedEvent actual) {
        this.actual = actual;
    }

    static ExternalTrainingProgramProposalReleaseFailedEventAssertion assertThatExternalTrainingProgramProposalReleaseFailedEvent(TrainingProgramProposalReleaseFailedEvent actual) {
        return new ExternalTrainingProgramProposalReleaseFailedEventAssertion(actual);
    }

    ExternalTrainingProgramProposalReleaseFailedEventAssertion hasTrainingProgramProposalId(UUID expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramProposalReleaseFailedEventAssertion hasReviewerId(UUID expected) {
        assertThat(actual.reviewerId()).isEqualTo(expected);
        return this;
    }

    ExternalTrainingProgramProposalReleaseFailedEventAssertion hasEventIdWithSameDataAs(EventId expected) {
        assertThat(actual.eventId().eventId()).isEqualTo(expected.eventId());
        assertThat(actual.eventId().traceId()).isEqualTo(expected.traceId());
        assertThat(actual.eventId().correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.eventId().creationDateTime()).isEqualTo(expected.creationDateTime());
        return this;
    }
}