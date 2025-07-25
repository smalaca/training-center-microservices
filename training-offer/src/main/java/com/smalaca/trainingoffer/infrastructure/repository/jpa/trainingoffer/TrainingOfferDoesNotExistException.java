package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import java.util.UUID;

public class TrainingOfferDoesNotExistException extends RuntimeException {
    TrainingOfferDoesNotExistException(UUID trainingOfferId) {
        super("Training Offer with id " + trainingOfferId + " does not exist.");
    }
}