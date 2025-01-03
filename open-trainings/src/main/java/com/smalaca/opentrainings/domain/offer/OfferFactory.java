package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

@Factory
public class OfferFactory {
    private final Clock clock;

    public OfferFactory(Clock clock) {
        this.clock = clock;
    }

    public Offer create(UUID trainingId, Price price) {
        return new Offer(trainingId, price, clock.now());
    }
}
