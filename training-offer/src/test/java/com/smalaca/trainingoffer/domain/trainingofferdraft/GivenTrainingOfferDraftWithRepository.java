package com.smalaca.trainingoffer.domain.trainingofferdraft;

class GivenTrainingOfferDraftWithRepository extends GivenTrainingOfferDraft {
    private final TrainingOfferDraftRepository repository;

    GivenTrainingOfferDraftWithRepository(TrainingOfferDraftRepository repository) {
        this.repository = repository;
    }

    @Override
    public GivenTrainingOfferDraft initiated() {
        super.initiated();
        saveTrainingOfferDraft();
        return this;
    }

    @Override
    public GivenTrainingOfferDraft published() {
        super.published();
        saveTrainingOfferDraft();
        return this;
    }

    private void saveTrainingOfferDraft() {
        TrainingOfferDraft trainingOfferDraft = getTrainingOfferDraft();
        repository.save(trainingOfferDraft);
    }
}