package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import org.springframework.stereotype.Component;

@Factory
@Component
public class TrainingOfferDraftFactory {
    public TrainingOfferDraft create(CreateTrainingOfferDraftCommand command) {
        return new TrainingOfferDraft.Builder()
                .withTrainingProgramId(command.trainingProgramId())
                .withTrainerId(command.trainerId())
                .withPrice(command.priceAmount(), command.priceCurrency())
                .withMinimumParticipants(command.minimumParticipants())
                .withMaximumParticipants(command.maximumParticipants())
                .withTrainingSessionPeriod(
                        command.startDate(),
                        command.endDate(),
                        command.startTime(),
                        command.endTime())
                .build();
    }
}