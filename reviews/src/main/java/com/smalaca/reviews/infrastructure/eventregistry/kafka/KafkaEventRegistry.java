package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import com.smalaca.schemaregistry.metadata.EventId;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventRegistry {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String proposalApprovedTopic;
    private final String proposalRejectedTopic;

    public KafkaEventRegistry(KafkaTemplate<String, Object> kafkaTemplate, String proposalApprovedTopic, String proposalRejectedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.proposalApprovedTopic = proposalApprovedTopic;
        this.proposalRejectedTopic = proposalRejectedTopic;
    }

    public void publish(ProposalApprovedEvent event) {
        kafkaTemplate.send(proposalApprovedTopic, asProposalApprovedEvent(event));
    }

    private com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent asProposalApprovedEvent(ProposalApprovedEvent event) {
        EventId eventId = convertEventId(event.eventId());
        return new com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent(
                eventId,
                event.proposalId(),
                event.reviewerId()
        );
    }

    public void publish(ProposalRejectedEvent event) {
        kafkaTemplate.send(proposalRejectedTopic, asProposalRejectedEvent(event));
    }

    private com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent asProposalRejectedEvent(ProposalRejectedEvent event) {
        EventId eventId = convertEventId(event.eventId());
        return new com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent(
                eventId,
                event.proposalId(),
                event.reviewerId()
        );
    }

    private EventId convertEventId(com.smalaca.reviews.domain.eventid.EventId eventId) {
        return new EventId(
                eventId.eventId(),
                eventId.traceId(),
                eventId.correlationId(),
                eventId.creationDateTime()
        );
    }
}