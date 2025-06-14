package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.AggregateRoot;

import java.util.UUID;

@AggregateRoot
public class TrainingOfferDraft {
    private final UUID trainingProgramId;
    private boolean published;

    public TrainingOfferDraft(UUID trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
        this.published = false;
    }

    public void publish() {
        this.published = true;
    }
}