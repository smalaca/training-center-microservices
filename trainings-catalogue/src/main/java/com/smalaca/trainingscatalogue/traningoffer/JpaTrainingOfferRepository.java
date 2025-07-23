package com.smalaca.trainingscatalogue.traningoffer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTrainingOfferRepository extends CrudRepository<TrainingOffer, UUID> {
    @Query("SELECT to.trainingOfferId as trainingOfferId, " +
           "to.trainerId as trainerId, " +
           "COALESCE(tp.name, 'NO NAME') as trainingProgramName, " +
           "to.startDate as startDate, " +
           "to.endDate as endDate " +
           "FROM TrainingOffer to " +
           "LEFT JOIN com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram tp " +
           "ON to.trainingProgramId = tp.trainingProgramId")
    List<TrainingOfferSummary> findAllTrainingOfferSummaries();
}