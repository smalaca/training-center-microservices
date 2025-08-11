package com.smalaca.schemaregistry.reviews.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalAssignedEventTest {

    @Test
    void shouldCreateProposalAssignedEvent() {
        EventId eventId = EventId.newEventId();
        UUID proposalId = UUID.randomUUID();
        UUID assignedReviewerId = UUID.randomUUID();

        ProposalAssignedEvent actual = new ProposalAssignedEvent(eventId, proposalId, assignedReviewerId);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        assertThat(actual.assignedReviewerId()).isEqualTo(assignedReviewerId);
    }
}