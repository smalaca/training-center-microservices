package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;

abstract public class GivenOffer {
    private final OfferFactory offerFactory;
    private final Clock clock;
    private final TrainingOfferCatalogue trainingOfferCatalogue;

    private UUID trainingId = randomId();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Price trainingPrice = randomPrice();
    private Offer offer;

    protected GivenOffer(OfferFactory offerFactory, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        this.offerFactory = offerFactory;
        this.clock = clock;
        this.trainingOfferCatalogue = trainingOfferCatalogue;
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
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(new TrainingDto(randomAvailability(), trainingPrice));
        offer = offerFactory.create(trainingId);

        return this;
    }

    private int randomAvailability() {
        return RandomUtils.secure().randomInt(1, 42);
    }

    protected Offer getOffer() {
        return offer;
    }
}
