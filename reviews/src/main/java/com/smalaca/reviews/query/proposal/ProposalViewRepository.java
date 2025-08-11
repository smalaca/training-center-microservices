package com.smalaca.reviews.query.proposal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

interface ProposalViewRepository extends CrudRepository<ProposalView, UUID> {
    @Query(value = "SELECT * FROM PROPOSALS p WHERE p.STATUS = 'QUEUED' AND p.LAST_ASSIGNMENT_DATE_TIME < :weekAgo", nativeQuery = true)
    Iterable<ProposalView> findAllQueuedOffersWithLastAssignmentOlderThan(@Param("weekAgo") LocalDateTime weekAgo);
}
