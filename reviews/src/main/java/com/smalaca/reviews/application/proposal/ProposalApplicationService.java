package com.smalaca.reviews.application.proposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class ProposalApplicationService {
    private final ProposalRepository repository;
    private final Clock clock;

    ProposalApplicationService(ProposalRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
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
    public void approve(UUID proposalId, UUID approverId) {
        Proposal proposal = repository.findById(proposalId);

        proposal.approve(approverId, clock);

        repository.save(proposal);
    }
}
