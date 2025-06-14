package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class TrainingOfferDraftApplicationService {
    private final TrainingOfferDraftRepository repository;
    private final EventRegistry eventRegistry;

    TrainingOfferDraftApplicationService(TrainingOfferDraftRepository repository, EventRegistry eventRegistry) {
        this.repository = repository;
        this.eventRegistry = eventRegistry;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void publish(UUID trainingOfferDraftId) {
        TrainingOfferDraft draft = repository.findById(trainingOfferDraftId);

        TrainingOfferPublishedEvent event = draft.publish();

        repository.save(draft);
        eventRegistry.publish(event);
    }
}
