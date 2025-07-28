package com.smalaca.trainingoffer.application.trainingoffer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingoffer.RescheduledTrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferDomainService;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class TrainingOfferApplicationService {
    private final TrainingOfferRepository repository;
    private final TrainingOfferFactory factory;
    private final EventRegistry eventRegistry;
    private final TrainingOfferDomainService trainingOfferDomainService;

    TrainingOfferApplicationService(
            TrainingOfferRepository repository, TrainingOfferFactory factory, TrainingOfferDomainService domainService, EventRegistry eventRegistry) {
        this.repository = repository;
        this.factory = factory;
        this.eventRegistry = eventRegistry;
        this.trainingOfferDomainService = domainService;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void create(TrainingOfferPublishedEvent event) {
        TrainingOffer trainingOffer = factory.create(event);
        
        repository.save(trainingOffer);
    }
    
    @Transactional
    @CommandOperation
    @DrivingPort
    public void confirmPrice(ConfirmTrainingPriceCommand command) {
        TrainingOffer trainingOffer = repository.findById(command.trainingOfferId());
        
        TrainingOfferEvent event = trainingOffer.confirmPrice(command);
        
        eventRegistry.publish(event);
    }
    
    @Transactional
    @CommandOperation
    @DrivingPort
    public void book(BookTrainingPlaceCommand command) {
        TrainingOffer trainingOffer = repository.findById(command.trainingOfferId());
        
        TrainingOfferEvent event = trainingOffer.book(command);
        
        repository.save(trainingOffer);
        eventRegistry.publish(event);
    }
    
    @Transactional
    @CommandOperation
    @DrivingPort
    public void reschedule(RescheduleTrainingOfferCommand command) {
        TrainingOffer existingTrainingOffer = repository.findById(command.trainingOfferId());

        RescheduledTrainingOffer rescheduledTrainingOffer = trainingOfferDomainService.reschedule(existingTrainingOffer, command);

        repository.save(existingTrainingOffer);
        repository.save(rescheduledTrainingOffer.trainingOffer());
        eventRegistry.publish(rescheduledTrainingOffer.event());
    }
}