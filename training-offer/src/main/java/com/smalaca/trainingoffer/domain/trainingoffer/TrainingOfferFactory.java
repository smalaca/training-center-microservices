package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

@Factory
public class TrainingOfferFactory {
    public TrainingOffer create(TrainingOfferPublishedEvent event) {
        return new TrainingOffer.Builder()
                .withTrainingOfferDraftId(event.trainingOfferDraftId())
                .withTrainingProgramId(event.trainingProgramId())
                .withTrainerId(event.trainerId())
                .withPrice(event.priceAmount(), event.priceCurrencyCode())
                .withMinimumParticipants(event.minimumParticipants())
                .withMaximumParticipants(event.maximumParticipants())
                .withTrainingSessionPeriod(
                        event.startDate(),
                        event.endDate(),
                        event.startTime(),
                        event.endTime())
                .build();
    }
}