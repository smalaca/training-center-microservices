package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class TrainingOfferDraftApplicationService {
    private final TrainingOfferDraftRepository repository;
    private final EventRegistry eventRegistry;
    private final TrainingOfferDraftFactory factory;

    TrainingOfferDraftApplicationService(
            TrainingOfferDraftRepository repository, 
            EventRegistry eventRegistry,
            TrainingOfferDraftFactory factory) {
        this.repository = repository;
        this.eventRegistry = eventRegistry;
        this.factory = factory;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID create(CreateTrainingOfferDraftCommand command) {
        TrainingOfferDraft draft = factory.create(command);
        
        return repository.save(draft);
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public UUID publish(UUID trainingOfferDraftId) {
        TrainingOfferDraft draft = repository.findById(trainingOfferDraftId);

        TrainingOfferPublishedEvent event = draft.publish();

        eventRegistry.publish(event);
        return event.trainingOfferId();
    }
    
    @Transactional
    @CommandOperation
    @DrivingPort
    public void published(TrainingOfferPublishedEvent event) {
        TrainingOfferDraft draft = repository.findById(event.trainingOfferDraftId());
        
        draft.published();
        
        repository.save(draft);
    }
}
