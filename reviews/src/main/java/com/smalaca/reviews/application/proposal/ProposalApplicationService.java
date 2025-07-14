package com.smalaca.reviews.application.proposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class ProposalApplicationService {
    private final ProposalRepository repository;
    private final Clock clock;
    private final EventRegistry eventRegistry;

    ProposalApplicationService(ProposalRepository repository, Clock clock, EventRegistry eventRegistry) {
        this.repository = repository;
        this.clock = clock;
        this.eventRegistry = eventRegistry;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void register(RegisterProposalCommand command) {
        Proposal proposal = Proposal.register(command);

        repository.save(proposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void approve(UUID proposalId, UUID reviewerId) {
        Proposal proposal = repository.findById(proposalId);

        ProposalApprovedEvent event = proposal.approve(reviewerId, clock);

        eventRegistry.publish(event);
        repository.save(proposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void reject(UUID proposalId, UUID reviewerId) {
        Proposal proposal = repository.findById(proposalId);

        ProposalRejectedEvent event = proposal.reject(reviewerId, clock);

        eventRegistry.publish(event);
        repository.save(proposal);
    }
}
