package com.smalaca.reviews.query.proposal;

import com.smalaca.architecture.cqrs.QueryOperation;
import com.smalaca.reviews.domain.clock.Clock;
import org.springframework.stereotype.Service;

@Service
public class ProposalQueryService {
    private final ProposalViewRepository repository;
    private final Clock clock;

    ProposalQueryService(ProposalViewRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @QueryOperation
    public Iterable<ProposalView> findAllToAssign() {
        return repository.findAllQueuedOffersWithLastAssignmentOlderThan(clock.now().minusMinutes(10));
    }
}
