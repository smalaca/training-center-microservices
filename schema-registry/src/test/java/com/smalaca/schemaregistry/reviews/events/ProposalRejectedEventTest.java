package com.smalaca.schemaregistry.reviews.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalRejectedEventTest {

    @Test
    void shouldCreateProposalRejectedEvent() {
        EventId eventId = EventId.newEventId();
        UUID proposalId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();

        ProposalRejectedEvent actual = new ProposalRejectedEvent(eventId, proposalId, reviewerId);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
    }
}