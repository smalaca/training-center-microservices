package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

import java.util.UUID;

@AggregateRoot
public class TrainingOfferDraft {
    private final UUID trainingOfferDraftId;
    private final UUID trainingProgramId;
    private boolean published;

    public TrainingOfferDraft(UUID trainingProgramId) {
        this.trainingOfferDraftId = UUID.randomUUID();
        this.trainingProgramId = trainingProgramId;
        this.published = false;
    }

    public TrainingOfferPublishedEvent publish() {
        this.published = true;
        return TrainingOfferPublishedEvent.create(trainingOfferDraftId, trainingProgramId);
    }
}
