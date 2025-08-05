package com.smalaca.reviews.application.proposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalFactory;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationLayer
public class ProposalApplicationService {
    private final ProposalFactory factory;
    private final ProposalRepository repository;
    private final Clock clock;
    private final EventRegistry eventRegistry;

    ProposalApplicationService(ProposalFactory factory, ProposalRepository repository, Clock clock, EventRegistry eventRegistry) {
        this.factory = factory;
        this.repository = repository;
        this.clock = clock;
        this.eventRegistry = eventRegistry;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void register(RegisterProposalCommand command) {
        Proposal proposal = factory.create(command);

        repository.save(proposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void approve(UUID proposalId, UUID reviewerId) {
        Proposal proposal = repository.findById(proposalId);

        Optional<ProposalApprovedEvent> event = proposal.approve(reviewerId, clock);

        event.ifPresent(eventRegistry::publish);
        repository.save(proposal);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void reject(UUID proposalId, UUID reviewerId) {
        Proposal proposal = repository.findById(proposalId);

        Optional<ProposalRejectedEvent> event = proposal.reject(reviewerId, clock);

        event.ifPresent(eventRegistry::publish);
        repository.save(proposal);
    }
}
