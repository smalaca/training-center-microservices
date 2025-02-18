package com.smalaca.opentrainings.domain.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;

public class GivenOffer {
    private final OfferFactory offerFactory;
    private final Clock clock;
    private final TrainingOfferCatalogue trainingOfferCatalogue;

    private UUID trainingId = randomId();
    private LocalDateTime creationDateTime = LocalDateTime.now();
    private Price trainingPrice = randomPrice();
    private Offer offer;

    GivenOffer(OfferFactory offerFactory, Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
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

    public GivenOffer rejected() {
        initiated();
        given(clock.now()).willReturn(creationDateTime.plusMinutes(20));
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(new TrainingDto(randomAvailability(), randomPrice()));
        offer.accept(null, trainingOfferCatalogue, null, clock);
        return this;
    }

    public GivenOffer accepted() {
        initiated();
        UUID participantId = UUID.randomUUID();
        AcceptOfferCommand command = AcceptOfferCommand.nextAfter(new PersonRegisteredEvent(EventId.newEventId(), getOfferId(), participantId), null);
        given(clock.now()).willReturn(creationDateTime.plusMinutes(1));
        given(trainingOfferCatalogue.book(new TrainingBookingDto(trainingId, participantId))).willReturn(TrainingBookingResponse.successful(trainingId, participantId));

        offer.accept(command, trainingOfferCatalogue, null, clock);
        return this;
    }

    public GivenOffer terminated() {
        initiated();
        given(clock.now()).willReturn(LocalDateTime.now());
        offer.terminate(clock);

        return this;
    }

    public GivenOffer declined() {
        initiated();
        offer.decline();

        return this;
    }

    public Offer getOffer() {
        return offer;
    }

    public OfferTestDto getDto() {
        return OfferTestDto.builder()
                .offerId(getOfferId())
                .trainingId(trainingId)
                .trainingPrice(trainingPrice)
                .creationDateTime(creationDateTime)
                .build();
    }

    protected UUID getOfferId() {
        return null;
    }
}
