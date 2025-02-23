package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomPrice;
import static org.mockito.BDDMockito.given;

class GivenOfferAcceptance {
    private final GivenOfferFactory givenOfferFactory;
    private final DiscountService discountService;
    private final TrainingOfferCatalogue trainingOfferCatalogue;
    private final OfferAcceptanceSagaEventTestListener testListener;
    private OfferTestDto offer;

    private GivenOfferAcceptance(
            GivenOfferFactory givenOfferFactory, DiscountService discountService, TrainingOfferCatalogue trainingOfferCatalogue,
            OfferAcceptanceSagaEventTestListener testListener) {
        this.givenOfferFactory = givenOfferFactory;
        this.discountService = discountService;
        this.trainingOfferCatalogue = trainingOfferCatalogue;
        this.testListener = testListener;
    }

    static GivenOfferAcceptance create(
            OfferRepository repository, DiscountService discountService, TrainingOfferCatalogue trainingOfferCatalogue,
            OfferAcceptanceSagaEventTestListener testListener) {
        return new GivenOfferAcceptance(GivenOfferFactory.create(repository), discountService, trainingOfferCatalogue, testListener);
    }

    OfferTestDto expiredOffer() {
        offer = givenOfferFactory.offer().createdMinutesAgo(13).initiated().getDto();
        return offer;
    }

    OfferTestDto initiatedOffer() {
        offer = givenOfferFactory.offer().initiated().getDto();
        return offer;
    }

    OfferTestDto declinedOffer() {
        offer = givenOfferFactory.offer().declined().getDto();
        return offer;
    }

    GivenOfferAcceptance discount(DiscountTestDto discountTestDto) {
        DiscountResponse response = discountTestDto.asDiscountResponse();
        DiscountCodeDto dto = discountTestDto.asDiscountCodeDto();
        given(discountService.calculatePriceFor(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance bookableTraining(UUID trainingId, UUID participantId) {
        TrainingBookingResponse response = TrainingBookingResponse.successful(trainingId, participantId);
        TrainingBookingDto dto = new TrainingBookingDto(trainingId, participantId);
        given(trainingOfferCatalogue.book(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance nonBookableTraining(UUID trainingId, UUID participantId) {
        TrainingBookingResponse response = TrainingBookingResponse.failed(trainingId, participantId);
        TrainingBookingDto dto = new TrainingBookingDto(trainingId, participantId);
        given(trainingOfferCatalogue.book(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance trainingPriceChanged(UUID trainingId) {
        TrainingDto trainingDto = new TrainingDto(randomAvailablePlaces(), randomPrice());
        given(trainingOfferCatalogue.detailsOf(trainingId)).willReturn(trainingDto);

        return this;
    }

    private int randomAvailablePlaces() {
        return RandomUtils.secure().randomInt(1, 42);
    }

    GivenOfferAcceptance alreadyRegisteredPersonFound(UUID offerId, UUID participantId) {
        testListener.willReturn(offerId, new AlreadyRegisteredPersonFoundEvent(EventId.newEventId(), offerId, participantId));
        return this;
    }

    GivenOfferAcceptance personRegistered(UUID offerId, UUID participantId) {
        testListener.willReturn(offerId, new PersonRegisteredEvent(EventId.newEventId(), offerId, participantId));
        return this;
    }
}
