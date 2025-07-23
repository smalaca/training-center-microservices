package com.smalaca.trainingscatalogue.trainingprogram;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTrainingProgramRepository extends CrudRepository<TrainingProgram, UUID> {
    @Query("SELECT tp.trainingProgramId as trainingProgramId, " +
           "tp.authorId as authorId, " +
           "tp.name as name " +
           "FROM TrainingProgram tp")
    List<TrainingProgramSummary> findAllTrainingProgramSummaries();
}