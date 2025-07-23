package com.smalaca.trainingscatalogue.traningoffer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
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
    
    @Query("SELECT to.trainingOfferId as trainingOfferId, " +
           "to.trainerId as trainerId, " +
           "to.trainingProgramId as trainingProgramId, " +
           "to.startDate as startDate, " +
           "to.endDate as endDate, " +
           "to.startTime as startTime, " +
           "to.endTime as endTime, " +
           "to.priceAmount as priceAmount, " +
           "to.priceCurrency as priceCurrency, " +
           "to.minimumParticipants as minimumParticipants, " +
           "to.maximumParticipants as maximumParticipants, " +
           "tp.name as name, " +
           "tp.agenda as agenda, " +
           "tp.plan as plan, " +
           "tp.description as description " +
           "FROM TrainingOffer to " +
           "LEFT JOIN com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram tp " +
           "ON to.trainingProgramId = tp.trainingProgramId " +
           "WHERE to.trainingOfferId = :trainingOfferId")
    Optional<TrainingOfferDetail> findTrainingOfferDetailById(@Param("trainingOfferId") UUID trainingOfferId);
}