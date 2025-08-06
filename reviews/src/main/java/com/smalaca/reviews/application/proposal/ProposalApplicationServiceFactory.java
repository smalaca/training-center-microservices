package com.smalaca.reviews.application.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.NoAssignmentPolicy;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalReviewAssignmentPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProposalApplicationServiceFactory {

    @Bean
    public ProposalApplicationService proposalApplicationService(ProposalRepository repository, Clock clock, EventRegistry eventRegistry) {
        ProposalReviewAssignmentPolicy assignmentPolicy = new NoAssignmentPolicy(clock);
        return new ProposalApplicationService(repository, clock, eventRegistry, assignmentPolicy);
    }
}