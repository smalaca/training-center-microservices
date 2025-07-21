package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;

import java.util.Optional;
import java.util.UUID;

public class SpringTrainingOfferCrudTestRepository {
    private final SpringTrainingOfferCrudRepository repository;

    SpringTrainingOfferCrudTestRepository(SpringTrainingOfferCrudRepository repository) {
        this.repository = repository;
    }

    public Optional<TrainingOffer> findById(UUID trainingOfferId) {
        return repository.findById(trainingOfferId);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}