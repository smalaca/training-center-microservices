package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalAssignedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import com.smalaca.schemaregistry.metadata.EventId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@DrivenAdapter
class KafkaEventRegistry implements EventRegistry {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String proposalApprovedTopic;
    private final String proposalAssignedTopic;
    private final String proposalRejectedTopic;

    KafkaEventRegistry(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.reviews.events.proposal-approved}") String proposalApprovedTopic,
            @Value("${kafka.topics.reviews.events.proposal-assigned}") String proposalAssignedTopic,
            @Value("${kafka.topics.reviews.events.proposal-rejected}") String proposalRejectedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.proposalApprovedTopic = proposalApprovedTopic;
        this.proposalAssignedTopic = proposalAssignedTopic;
        this.proposalRejectedTopic = proposalRejectedTopic;
    }

    @Override
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

    @Override
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

    @Override
    public void publish(ProposalAssignedEvent event) {
        kafkaTemplate.send(proposalAssignedTopic, asProposalAssignedEvent(event));
    }

    private com.smalaca.schemaregistry.reviews.events.ProposalAssignedEvent asProposalAssignedEvent(ProposalAssignedEvent event) {
        EventId eventId = convertEventId(event.eventId());
        return new com.smalaca.schemaregistry.reviews.events.ProposalAssignedEvent(
                eventId,
                event.proposalId(),
                event.assignedReviewerId()
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