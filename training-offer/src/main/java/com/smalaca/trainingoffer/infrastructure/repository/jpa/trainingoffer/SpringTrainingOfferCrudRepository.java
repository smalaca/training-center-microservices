package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface SpringTrainingOfferCrudRepository extends CrudRepository<TrainingOffer, UUID> {
}