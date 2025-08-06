package com.smalaca.reviews.application.proposal;

import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ReviewerAssignmentPolicy;
import com.smalaca.reviews.domain.proposal.ReviewerAssignmentPolicyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProposalApplicationServiceFactory {

    @Bean
    public ProposalApplicationService proposalApplicationService(ProposalRepository repository, Clock clock, EventRegistry eventRegistry) {
        ReviewerAssignmentPolicy reviewerAssignmentPolicy = new ReviewerAssignmentPolicyFactory().reviewerAssignmentPolicy(clock);
        return new ProposalApplicationService(repository, clock, eventRegistry, reviewerAssignmentPolicy);
    }
}