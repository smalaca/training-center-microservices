package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

@Factory
public class TrainingOfferDraftFactory {
    public TrainingOfferDraft create(CreateTrainingOfferDraftCommand command) {
        return new TrainingOfferDraft.Builder()
                .withTrainingProgramId(command.trainingProgramId())
                .withTrainerId(command.trainerId())
                .withPrice(asPrice(command))
                .withMinimumParticipants(command.minimumParticipants())
                .withMaximumParticipants(command.maximumParticipants())
                .withTrainingSessionPeriod(asTrainingSessionPeriod(command))
                .build();
    }

    private TrainingSessionPeriod asTrainingSessionPeriod(CreateTrainingOfferDraftCommand command) {
        return new TrainingSessionPeriod(command.startDate(), command.endDate(), command.startTime(), command.endTime());
    }

    private Price asPrice(CreateTrainingOfferDraftCommand command) {
        return Price.of(command.priceAmount(), command.priceCurrency());
    }
}