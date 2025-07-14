package com.smalaca.reviews.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;

@DrivenPort
public interface EventRegistry {
    void publish(ProposalApprovedEvent event);
}