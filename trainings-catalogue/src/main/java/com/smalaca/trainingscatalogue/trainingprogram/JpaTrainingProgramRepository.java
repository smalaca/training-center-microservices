package com.smalaca.trainingscatalogue.trainingprogram;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JpaTrainingProgramRepository extends CrudRepository<TrainingProgram, UUID> {
}