package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;

public class SpringTrainingOfferCrudTestRepository {
    private final SpringTrainingOfferCrudRepository repository;

    SpringTrainingOfferCrudTestRepository(SpringTrainingOfferCrudRepository repository) {
        this.repository = repository;
    }

    public Iterable<TrainingOffer> findAll() {
        return repository.findAll();
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}