package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import org.springframework.stereotype.Component;

@Component
@DrivenAdapter
class DummyEventRegistry implements EventRegistry {
    @Override
    public void publish(ProposalApprovedEvent event) {

    }

    @Override
    public void publish(ProposalRejectedEvent event) {

    }
}