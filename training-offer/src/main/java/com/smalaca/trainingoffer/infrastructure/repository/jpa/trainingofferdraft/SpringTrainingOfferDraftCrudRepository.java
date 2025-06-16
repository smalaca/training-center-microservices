package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft;

import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringTrainingOfferDraftCrudRepository extends CrudRepository<TrainingOfferDraft, UUID> {
}