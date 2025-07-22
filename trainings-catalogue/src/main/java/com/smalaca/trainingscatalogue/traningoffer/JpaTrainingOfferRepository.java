package com.smalaca.trainingscatalogue.traningoffer;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface JpaTrainingOfferRepository extends CrudRepository<TrainingOffer, UUID> {
}