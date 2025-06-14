package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.domaindrivendesign.DomainRepository;

import java.util.UUID;

@DrivenPort
@DomainRepository
public interface TrainingOfferDraftRepository {
    TrainingOfferDraft findById(UUID trainingOfferDraftId);

    void save(TrainingOfferDraft trainingOfferDraft);
}
