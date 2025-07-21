package com.smalaca.trainingoffer.application.trainingoffer;

import com.smalaca.architecture.cqrs.CommandOperation;
import com.smalaca.architecture.portsandadapters.DrivingPort;
import com.smalaca.domaindrivendesign.ApplicationLayer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.transaction.annotation.Transactional;

@ApplicationLayer
public class TrainingOfferApplicationService {
    private final TrainingOfferRepository repository;
    private final TrainingOfferFactory factory;

    TrainingOfferApplicationService(TrainingOfferRepository repository, TrainingOfferFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    @Transactional
    @CommandOperation
    @DrivingPort
    public void create(TrainingOfferPublishedEvent event) {
        TrainingOffer trainingOffer = factory.create(event);
        
        repository.save(trainingOffer);
    }
}