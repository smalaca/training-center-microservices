package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@DrivenAdapter
public class DummyTrainingOfferRepository implements TrainingOfferRepository {
    @Override
    public UUID save(TrainingOffer trainingOffer) {
        return null;
    }
}
