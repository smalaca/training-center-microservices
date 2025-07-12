package com.smalaca.trainingprograms.infrastructure.repository.jpa.trainingprogram;

import com.smalaca.trainingprograms.domain.trainingprogram.TrainingProgram;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringTrainingProgramCrudRepository extends CrudRepository<TrainingProgram, UUID> {
}