package com.smalaca.reviews.domain.proposal;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

@DomainRepository
@DrivenPort
public interface ProposalRepository {
    void save(Proposal proposal);
}