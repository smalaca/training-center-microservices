package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;

import java.util.UUID;

import static org.mockito.Mockito.mock;

public class GivenOfferFactory {
    private final Clock clock;
    private final OfferRepository offerRepository;
    private final OfferFactory offerFactory;

    private GivenOfferFactory(OfferRepository offerRepository, Clock clock, OfferFactory offerFactory) {
        this.offerRepository = offerRepository;
        this.offerFactory = offerFactory;
        this.clock = clock;
    }

    public static GivenOfferFactory create(OfferRepository offerRepository) {
        Clock clock = mock(Clock.class);
        OfferFactory offerFactory = new OfferFactory(clock);

        return new GivenOfferFactory(offerRepository, clock, offerFactory);
    }

    public GivenOffer offer(UUID offerId) {
        return new GivenOfferWithMockRepository(offerRepository, clock, offerFactory, offerId);
    }
}
