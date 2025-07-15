package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.reviews.domain.proposal.Proposal;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface SpringProposalCrudRepository extends CrudRepository<Proposal, UUID> {
}