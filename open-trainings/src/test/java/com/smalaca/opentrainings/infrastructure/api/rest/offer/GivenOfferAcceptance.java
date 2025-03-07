package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static org.mockito.BDDMockito.given;

class GivenOfferAcceptance {
    private final GivenOfferFactory givenOfferFactory;
    private final DiscountService discountService;
    private final TrainingOfferCatalogue trainingOfferCatalogue;
    private final OfferAcceptanceSagaEventTestListener testListener;
    private final UUID participantId = randomId();
    private final Price newPrice = randomPrice();

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

    GivenOfferAcceptance expiredOffer() {
        offer = givenOfferFactory.offer().createdMinutesAgo(13).initiated().getDto();
        return this;
    }

    GivenOfferAcceptance initiatedOffer() {
        offer = givenOfferFactory.offer().initiated().getDto();
        return this;
    }

    GivenOfferAcceptance declinedOffer() {
        offer = givenOfferFactory.offer().declined().getDto();
        return this;
    }

    GivenOfferAcceptance discount(String discountCode) {
        DiscountResponse response = DiscountResponse.successful(newPrice);
        DiscountCodeDto dto = new DiscountCodeDto(participantId, offer.getTrainingId(), offer.getTrainingPrice(), discountCode);
        given(discountService.calculatePriceFor(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance bookableTraining() {
        TrainingBookingResponse response = TrainingBookingResponse.successful(offer.getTrainingId(), participantId);
        TrainingBookingDto dto = new TrainingBookingDto(offer.getTrainingId(), participantId);
        given(trainingOfferCatalogue.book(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance nonBookableTraining() {
        TrainingBookingResponse response = TrainingBookingResponse.failed(offer.getTrainingId(), participantId);
        TrainingBookingDto dto = new TrainingBookingDto(offer.getTrainingId(), participantId);
        given(trainingOfferCatalogue.book(dto)).willReturn(response);

        return this;
    }

    GivenOfferAcceptance trainingPriceNotChanged() {
        OfferAcceptanceSagaEvent event = new TrainingPriceNotChangedEvent(newEventId(), offer.getOfferId(), offer.getTrainingId());
        testListener.willReturnAfterConfirmTrainingPriceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance trainingPriceChanged(BigDecimal price, String currency) {
        OfferAcceptanceSagaEvent event = new TrainingPriceChangedEvent(newEventId(), offer.getOfferId(), offer.getTrainingId(), price, currency);
        testListener.willReturnAfterConfirmTrainingPriceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance alreadyRegisteredPersonFound() {
        AlreadyRegisteredPersonFoundEvent event = new AlreadyRegisteredPersonFoundEvent(newEventId(), offer.getOfferId(), participantId);
        testListener.willReturnAfterRegisterPersonCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance personRegistered() {
        PersonRegisteredEvent event = new PersonRegisteredEvent(newEventId(), offer.getOfferId(), participantId);
        testListener.willReturnAfterRegisterPersonCommand(offer.getOfferId(), event);

        return this;
    }

    OfferTestDto getOffer() {
        return offer;
    }
}
