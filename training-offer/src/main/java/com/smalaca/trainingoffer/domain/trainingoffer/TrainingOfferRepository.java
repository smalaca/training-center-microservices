package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.UUID;

@DrivenPort
@DomainRepository
public interface TrainingOfferRepository {
    TrainingOffer findById(UUID trainingOfferId);
    
    void save(TrainingOffer trainingOffer);
}