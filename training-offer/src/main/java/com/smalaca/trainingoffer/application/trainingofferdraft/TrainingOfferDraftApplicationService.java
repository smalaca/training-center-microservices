package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationLayer
public class TrainingOfferDraftApplicationService {
    private final TrainingOfferDraftRepository repository;

    TrainingOfferDraftApplicationService(TrainingOfferDraftRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void publish(UUID trainingOfferDraftId) {
        TrainingOfferDraft draft = repository.findById(trainingOfferDraftId);

        draft.publish();

        repository.save(draft);
    }
}