package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.eventid.EventId;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class DummyEventRegistryTest {

    private final DummyEventRegistry eventRegistry = new DummyEventRegistry();

    @Test
    void shouldNotThrowExceptionWhenPublishingProposalApprovedEvent() {
        ProposalApprovedEvent event = createProposalApprovedEvent();

        assertThatCode(() -> eventRegistry.publish(event))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldNotThrowExceptionWhenPublishingProposalRejectedEvent() {
        ProposalRejectedEvent event = createProposalRejectedEvent();

        assertThatCode(() -> eventRegistry.publish(event))
                .doesNotThrowAnyException();
    }

    private ProposalApprovedEvent createProposalApprovedEvent() {
        return new ProposalApprovedEvent(
                createEventId(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private ProposalRejectedEvent createProposalRejectedEvent() {
        return new ProposalRejectedEvent(
                createEventId(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private EventId createEventId() {
        return new EventId(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now()
        );
    }
}