package com.smalaca.trainingscatalogue.trainingoffer;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingOfferService {
    private final JpaTrainingOfferRepository trainingOfferRepository;

    TrainingOfferService(JpaTrainingOfferRepository trainingOfferRepository) {
        this.trainingOfferRepository = trainingOfferRepository;
    }

    @Transactional
    public void handle(TrainingOfferPublishedEvent event) {
        TrainingOffer trainingOffer = new TrainingOffer(event);
        trainingOfferRepository.save(trainingOffer);
    }
}