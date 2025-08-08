package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.clock.Clock;
import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalRepository;
import com.smalaca.reviews.domain.proposal.ProposalStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaProposalRepository implements ProposalRepository {
    private final SpringProposalCrudRepository repository;
    private final Clock clock;

    JpaProposalRepository(SpringProposalCrudRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public void save(Proposal proposal) {
        repository.save(proposal);
    }

    @Override
    public Proposal findById(UUID proposalId) {
        return repository.findById(proposalId).orElseThrow(() -> new ProposalDoesNotExistException(proposalId));
    }

    @Override
    public List<Proposal> findProposalsForAssignment() {
        LocalDateTime weekAgo = clock.now().minusWeeks(1);
        return repository.findProposalsForAssignment(
                ProposalStatus.REGISTERED, 
                ProposalStatus.QUEUED, 
                weekAgo
        );
    }
}