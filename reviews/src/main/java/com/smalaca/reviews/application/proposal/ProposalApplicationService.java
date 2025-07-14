package com.smalaca.reviews.application.proposal;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.commands.RegisterProposalCommand;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class ProposalApplicationService {
    private final ProposalRepository repository;

    ProposalApplicationService(ProposalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void register(RegisterProposalCommand command) {
        Proposal proposal = new Proposal(command);

        repository.save(proposal);
    }
}