package com.smalaca.trainingoffer.query.trainingofferdraft;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface TrainingOfferDraftViewRepository extends CrudRepository<TrainingOfferDraftView, UUID> {
}