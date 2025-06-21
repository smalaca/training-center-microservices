package com.smalaca.trainingoffer.query.trainingofferdraft;

import com.smalaca.architecture.cqrs.QueryOperation;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TrainingOfferDraftQueryService {
    private final TrainingOfferDraftViewRepository repository;

    TrainingOfferDraftQueryService(TrainingOfferDraftViewRepository repository) {
        this.repository = repository;
    }

    @QueryOperation
    public Iterable<TrainingOfferDraftView> findAll() {
        return repository.findAll();
    }

    @QueryOperation
    public Optional<TrainingOfferDraftView> findById(UUID trainingOfferDraftId) {
        return repository.findById(trainingOfferDraftId);
    }
}