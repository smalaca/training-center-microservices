package com.smalaca.trainingmanagement.domain.proposal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrainingProposalRepository extends JpaRepository<TrainingProposal, UUID> {
}