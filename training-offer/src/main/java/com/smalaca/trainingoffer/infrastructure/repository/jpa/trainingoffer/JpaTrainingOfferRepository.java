package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import org.springframework.stereotype.Repository;

@Repository
@DrivenAdapter
public class JpaTrainingOfferRepository implements TrainingOfferRepository {
    private final SpringTrainingOfferCrudRepository repository;

    JpaTrainingOfferRepository(SpringTrainingOfferCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TrainingOffer trainingOffer) {
        repository.save(trainingOffer);
    }
}