package com.smalaca.schemaregistry.reviews.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProposalApprovedEventTest {

    @Test
    void shouldCreateProposalApprovedEvent() {
        EventId eventId = EventId.newEventId();
        UUID proposalId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();

        ProposalApprovedEvent actual = new ProposalApprovedEvent(eventId, proposalId, reviewerId);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.proposalId()).isEqualTo(proposalId);
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
    }
}