package com.smalaca.reviews.query.proposal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

interface ProposalViewRepository extends CrudRepository<ProposalView, UUID> {
    @Query("SELECT p FROM ProposalView p WHERE p.status = 'QUEUED' AND p.lastAssignmentDateTime < :tenMinutesAgo")
    Iterable<ProposalView> findAllQueuedOffersWithLastAssignmentOlderThan(@Param("tenMinutesAgo") LocalDateTime tenMinutesAgo);
}
