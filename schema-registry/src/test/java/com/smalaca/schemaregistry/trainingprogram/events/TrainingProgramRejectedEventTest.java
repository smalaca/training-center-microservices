package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramRejectedEventTest {

    @Test
    void shouldCreateTrainingProgramRejectedEvent() {
        EventId eventId = EventId.newEventId();
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();

        TrainingProgramRejectedEvent actual = new TrainingProgramRejectedEvent(eventId, trainingProgramProposalId, reviewerId);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
    }
}