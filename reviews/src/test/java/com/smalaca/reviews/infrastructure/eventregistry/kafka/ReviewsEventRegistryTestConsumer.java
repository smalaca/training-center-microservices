package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent;
import com.smalaca.schemaregistry.reviews.events.ProposalAssignedEvent;
import com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class ReviewsEventRegistryTestConsumer {
    private final Map<UUID, ProposalApprovedEvent> proposalApprovedEvents = new HashMap<>();
    private final Map<UUID, ProposalAssignedEvent> proposalAssignedEvents = new HashMap<>();
    private final Map<UUID, ProposalRejectedEvent> proposalRejectedEvents = new HashMap<>();

    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-approved}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consume(ProposalApprovedEvent event) {
        proposalApprovedEvents.put(event.proposalId(), event);
    }

    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-assigned}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consume(ProposalAssignedEvent event) {
        proposalAssignedEvents.put(event.proposalId(), event);
    }

    @KafkaListener(
            topics = "${kafka.topics.reviews.events.proposal-rejected}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void consume(ProposalRejectedEvent event) {
        proposalRejectedEvents.put(event.proposalId(), event);
    }

    Optional<ProposalApprovedEvent> proposalApprovedEventFor(UUID proposalId) {
        return Optional.ofNullable(proposalApprovedEvents.get(proposalId));
    }

    Optional<ProposalAssignedEvent> proposalAssignedEventFor(UUID proposalId) {
        return Optional.ofNullable(proposalAssignedEvents.get(proposalId));
    }

    Optional<ProposalRejectedEvent> proposalRejectedEventFor(UUID proposalId) {
        return Optional.ofNullable(proposalRejectedEvents.get(proposalId));
    }
}