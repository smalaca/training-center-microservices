package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

@Factory
public class TrainingOfferFactory {
    public TrainingOffer create(TrainingOfferPublishedEvent event) {
        return new TrainingOffer.Builder()
                .withTrainingOfferId(event.trainingOfferId())
                .withTrainingOfferDraftId(event.trainingOfferDraftId())
                .withTrainingProgramId(event.trainingProgramId())
                .withTrainerId(event.trainerId())
                .withPrice(asPrice(event))
                .withMinimumParticipants(event.minimumParticipants())
                .withMaximumParticipants(event.maximumParticipants())
                .withTrainingSessionPeriod(asTrainingSessionPeriod(event))
                .build();
    }

    private Price asPrice(TrainingOfferPublishedEvent event) {
        return Price.of(event.priceAmount(), event.priceCurrencyCode());
    }

    private TrainingSessionPeriod asTrainingSessionPeriod(TrainingOfferPublishedEvent event) {
        return new TrainingSessionPeriod(event.startDate(), event.endDate(), event.startTime(), event.endTime());
    }
}