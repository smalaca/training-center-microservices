package com.smalaca.reviews.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalAssignedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;

@DrivenPort
public interface EventRegistry {
    void publish(ProposalApprovedEvent event);
    void publish(ProposalAssignedEvent event);
    void publish(ProposalRejectedEvent event);
}
