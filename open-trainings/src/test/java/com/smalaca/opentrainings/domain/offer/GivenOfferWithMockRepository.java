package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

public class GivenOfferWithMockRepository extends GivenOffer {
    private final OfferRepository offerRepository;
    private final UUID offerId;

    public GivenOfferWithMockRepository(OfferRepository offerRepository, Clock clock, OfferFactory offerFactory, UUID offerId) {
        super(clock, offerFactory);
        this.offerRepository = offerRepository;
        this.offerId = offerId;
    }

    @Override
    public GivenOfferWithMockRepository initiated() {
        super.initiated();
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
}
