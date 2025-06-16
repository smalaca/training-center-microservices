package com.smalaca.trainingoffer.domain.trainingofferdraft;

import java.util.UUID;

public class TrainingOfferDraftAlreadyPublishedException extends RuntimeException {
    TrainingOfferDraftAlreadyPublishedException(UUID trainingOfferDraftId) {
        super("Training offer draft: " + trainingOfferDraftId + " already published.");
    }
}