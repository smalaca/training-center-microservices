package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposal;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SpringTrainingProgramProposalCrudRepository extends CrudRepository<TrainingProgramProposal, UUID> {
}