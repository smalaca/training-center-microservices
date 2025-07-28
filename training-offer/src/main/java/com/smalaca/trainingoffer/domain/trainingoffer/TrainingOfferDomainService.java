package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.DomainService;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent;

@DomainService
public class TrainingOfferDomainService {
    private final TrainingOfferFactory factory;

    public TrainingOfferDomainService(TrainingOfferFactory factory) {
        this.factory = factory;
    }

    public RescheduledTrainingOffer reschedule(TrainingOffer trainingOffer, RescheduleTrainingOfferCommand command) {
        TrainingOfferRescheduledEvent trainingOfferRescheduledEvent = trainingOffer.reschedule(command);
        TrainingOffer newtrainingOffer = factory.create(trainingOfferRescheduledEvent);

        return new RescheduledTrainingOffer(newtrainingOffer, trainingOfferRescheduledEvent);
    }
}
