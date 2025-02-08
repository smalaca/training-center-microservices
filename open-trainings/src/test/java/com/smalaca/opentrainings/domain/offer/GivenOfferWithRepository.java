package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

import java.util.UUID;

class GivenOfferWithRepository extends GivenOffer {
    private final OfferRepository offerRepository;
    private UUID offerId;

    public GivenOfferWithRepository(OfferFactory offerFactory, OfferRepository offerRepository, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        super(offerFactory, clock, trainingOfferCatalogue);
        this.offerRepository = offerRepository;
    }

    @Override
    public GivenOffer initiated() {
        super.initiated();
        saveOffer();
        return this;
    }

    @Override
    public GivenOffer rejected() {
        super.rejected();
        saveOffer();
        return this;
    }

    @Override
    public GivenOffer accepted() {
        super.accepted();
        saveOffer();
        return this;
    }

    @Override
    public GivenOffer terminated() {
        super.terminated();
        saveOffer();
        return this;
    }

    @Override
    public GivenOffer declined() {
        super.declined();
        saveOffer();
        return this;
    }

    private void saveOffer() {
        offerId = offerRepository.save(getOffer());
    }

    @Override
    protected UUID getOfferId() {
        return offerId;
    }
}
