package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.price.Price;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;

abstract public class GivenOffer {
    private final Clock clock;
    private final OfferFactory offerFactory;

    private UUID trainingId = randomId();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Price trainingPrice = randomPrice();
    private Offer offer;

    protected GivenOffer(Clock clock, OfferFactory offerFactory) {
        this.clock = clock;
        this.offerFactory = offerFactory;
    }

    public GivenOffer trainingId(UUID trainingId) {
        this.trainingId = trainingId;
        return this;
    }

    public GivenOffer trainingPrice(Price trainingPrice) {
        this.trainingPrice = trainingPrice;
        return this;
    }

    public GivenOffer createdMinutesAgo(int minutes) {
        this.creationDateTime = LocalDateTime.now().minusMinutes(minutes);
        return this;
    }

    public GivenOffer initiated() {
        given(clock.now()).willReturn(creationDateTime);
        offer = offerFactory.create(trainingId, trainingPrice);

        return this;
    }
    
    protected Offer getOffer() {
        return offer;
    }
}
