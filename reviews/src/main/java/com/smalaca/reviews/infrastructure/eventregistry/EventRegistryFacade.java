package com.smalaca.reviews.infrastructure.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import com.smalaca.reviews.infrastructure.eventregistry.kafka.KafkaEventRegistry;

@DrivenAdapter
class EventRegistryFacade implements EventRegistry {
    private final KafkaEventRegistry kafkaEventRegistry;

    EventRegistryFacade(KafkaEventRegistry kafkaEventRegistry) {
        this.kafkaEventRegistry = kafkaEventRegistry;
    }

    @Override
    public void publish(ProposalApprovedEvent event) {
        kafkaEventRegistry.publish(event);
    }

    @Override
    public void publish(ProposalRejectedEvent event) {
        kafkaEventRegistry.publish(event);
    }
}
