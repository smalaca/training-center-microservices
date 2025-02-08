package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

class GivenOfferWithMockRepository extends GivenOffer {
    private final OfferRepository offerRepository;
    private final UUID offerId;

    GivenOfferWithMockRepository(OfferFactory offerFactory, OfferRepository offerRepository, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue, UUID offerId) {
        super(offerFactory, clock, trainingOfferCatalogue);
        this.offerRepository = offerRepository;
        this.offerId = offerId;
    }

    @Override
    public GivenOfferWithMockRepository initiated() {
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
        Offer offer = getOffer();
        assignOfferId(offer);
        given(offerRepository.findById(this.offerId)).willReturn(offer);
    }

    private GivenOfferWithMockRepository assignOfferId(Offer offer) {
        try {
            Field offerIdField = offer.getClass().getDeclaredField("offerId");
            offerIdField.setAccessible(true);
            offerIdField.set(offer, this.offerId);
            return this;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UUID getOfferId() {
        return this.offerId;
    }
}
