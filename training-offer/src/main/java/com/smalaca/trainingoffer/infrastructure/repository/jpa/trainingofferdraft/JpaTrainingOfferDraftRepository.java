package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class JpaTrainingOfferDraftRepository implements TrainingOfferDraftRepository {
    private final SpringTrainingOfferDraftCrudRepository repository;

    JpaTrainingOfferDraftRepository(SpringTrainingOfferDraftCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public TrainingOfferDraft findById(UUID trainingOfferDraftId) {
        return repository.findById(trainingOfferDraftId)
                .orElseThrow(() -> new TrainingOfferDraftDoesNotExistException(trainingOfferDraftId));
    }

    @Override
    public void save(TrainingOfferDraft trainingOfferDraft) {
        repository.save(trainingOfferDraft);
    }
}