package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.offer.OfferFactory.offerFactory;
import static org.mockito.Mockito.mock;

public class GivenOfferFactory {
    private final OfferFactory offerFactory;
    private final OfferRepository offerRepository;
    private final Clock clock;
    private final TrainingOfferCatalogue trainingOfferCatalogue;

    private GivenOfferFactory(
            OfferFactory offerFactory, OfferRepository offerRepository, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        this.offerFactory = offerFactory;
        this.offerRepository = offerRepository;
        this.clock = clock;
        this.trainingOfferCatalogue = trainingOfferCatalogue;
    }

    public static GivenOfferFactory create(OfferRepository offerRepository) {
        Clock clock = mock(Clock.class);
        TrainingOfferCatalogue trainingOfferCatalogue = mock(TrainingOfferCatalogue.class);
        OfferFactory offerFactory = offerFactory(trainingOfferCatalogue, clock);

        return new GivenOfferFactory(offerFactory, offerRepository, clock, trainingOfferCatalogue);
    }

    public GivenOffer offer(UUID offerId) {
        return new GivenOfferWithMockRepository(offerFactory, offerRepository, clock, trainingOfferCatalogue, offerId);
    }
}
