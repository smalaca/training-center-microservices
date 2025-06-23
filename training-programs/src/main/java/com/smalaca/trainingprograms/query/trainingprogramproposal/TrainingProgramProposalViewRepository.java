package com.smalaca.trainingprograms.query.trainingprogramproposal;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface TrainingProgramProposalViewRepository extends CrudRepository<TrainingProgramProposalView, UUID> {
}