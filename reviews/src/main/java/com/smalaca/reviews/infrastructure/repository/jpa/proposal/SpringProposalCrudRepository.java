package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.reviews.domain.proposal.Proposal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface SpringProposalCrudRepository extends CrudRepository<Proposal, UUID> {
}