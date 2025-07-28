package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent;

public record RescheduledTrainingOffer(TrainingOffer trainingOffer, TrainingOfferRescheduledEvent event) {
}
