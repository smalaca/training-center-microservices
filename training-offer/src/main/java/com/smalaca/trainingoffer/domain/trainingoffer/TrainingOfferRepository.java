package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

@DrivenPort
@DomainRepository
public interface TrainingOfferRepository {
    void save(TrainingOffer trainingOffer);
}