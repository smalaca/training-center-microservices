package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaProposalRepository implements ProposalRepository {
    private final SpringProposalCrudRepository repository;

    JpaProposalRepository(SpringProposalCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Proposal proposal) {
        repository.save(proposal);
    }

    @Override
    public Proposal findById(UUID proposalId) {
        return repository.findById(proposalId).orElseThrow(() -> new ProposalDoesNotExistException(proposalId));
    }
}