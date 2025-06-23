package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface SpringTrainingProgramProposalCrudRepository extends CrudRepository<TrainingProgramProposal, UUID> {
}