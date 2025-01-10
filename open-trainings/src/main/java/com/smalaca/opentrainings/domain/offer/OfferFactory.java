package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

import java.util.UUID;

@Factory
public class OfferFactory {
    private final TrainingOfferCatalogue trainingOfferCatalogue;
    private final OfferNumberFactory offerNumberFactory;
    private final Clock clock;

    private OfferFactory(TrainingOfferCatalogue trainingOfferCatalogue, OfferNumberFactory offerNumberFactory, Clock clock) {
        this.trainingOfferCatalogue = trainingOfferCatalogue;
        this.offerNumberFactory = offerNumberFactory;
        this.clock = clock;
    }

    public static OfferFactory offerFactory(TrainingOfferCatalogue trainingOfferCatalogue, Clock clock) {
        return new OfferFactory(trainingOfferCatalogue, new OfferNumberFactory(clock), clock);
    }

    public Offer create(UUID trainingId) {
        TrainingDto trainingDto = trainingOfferCatalogue.detailsOf(trainingId);

        if (trainingDto.hasNoAvailablePlaces()) {
            throw new NoAvailablePlacesException(trainingId);
        }

        return Offer.initiate(trainingId, offerNumberFactory.create(), trainingDto.price(), clock.now());
    }
}
