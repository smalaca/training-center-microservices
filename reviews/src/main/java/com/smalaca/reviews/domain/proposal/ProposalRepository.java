package com.smalaca.reviews.domain.proposal;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.List;
import java.util.UUID;

@DomainRepository
@DrivenPort
public interface ProposalRepository {
    void save(Proposal proposal);

    Proposal findById(UUID proposalId);
    
    List<Proposal> findProposalsForAssignment();
}
