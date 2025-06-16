package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft;

import java.util.UUID;

public class TrainingOfferDraftDoesNotExistException extends RuntimeException {
    TrainingOfferDraftDoesNotExistException(UUID trainingOfferDraftId) {
        super("Training Offer Draft with id " + trainingOfferDraftId + " does not exist.");
    }
}