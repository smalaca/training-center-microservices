package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramProposalReleaseFailedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateTrainingProgramProposalReleaseFailedEvent() {
        EventId eventId = EventId.newEventId();
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();

        TrainingProgramProposalReleaseFailedEvent actual = new TrainingProgramProposalReleaseFailedEvent(
                eventId, trainingProgramProposalId, reviewerId);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
    }
}