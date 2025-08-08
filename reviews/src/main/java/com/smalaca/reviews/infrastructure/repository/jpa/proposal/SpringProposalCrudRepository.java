package com.smalaca.reviews.infrastructure.repository.jpa.proposal;

import com.smalaca.reviews.domain.proposal.Proposal;
import com.smalaca.reviews.domain.proposal.ProposalStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

interface SpringProposalCrudRepository extends CrudRepository<Proposal, UUID> {
    @Query("SELECT p FROM Proposal p WHERE (p.status = :registeredStatus AND p.lastAssignmentDateTime IS NULL) OR (p.status = :queuedStatus AND p.lastAssignmentDateTime < :weekAgo)")
    List<Proposal> findProposalsForAssignment(@Param("registeredStatus") ProposalStatus registeredStatus, 
                                             @Param("queuedStatus") ProposalStatus queuedStatus,
                                             @Param("weekAgo") LocalDateTime weekAgo);
}