package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;

class GivenOfferAcceptance {
    private final GivenOfferFactory givenOfferFactory;
    private final OfferAcceptanceSagaEventTestListener testListener;
    private final UUID participantId = randomId();

    private OfferTestDto offer;

    private GivenOfferAcceptance(GivenOfferFactory givenOfferFactory, OfferAcceptanceSagaEventTestListener testListener) {
        this.givenOfferFactory = givenOfferFactory;
        this.testListener = testListener;
    }

    static GivenOfferAcceptance create(OfferRepository repository, OfferAcceptanceSagaEventTestListener testListener) {
        return new GivenOfferAcceptance(GivenOfferFactory.create(repository), testListener);
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

    GivenOfferAcceptance discountAlreadyUsed(String discountCode) {
        DiscountCodeAlreadyUsedEvent event = new DiscountCodeAlreadyUsedEvent(externalNewEventId(), offer.getOfferId(), participantId, offer.getTrainingId(), discountCode);
        testListener.willReturnDiscountCodeAlreadyUsedEventAfterUseDiscountCodeCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance discountUsed(String discountCode) {
        DiscountCodeUsedEvent event = new DiscountCodeUsedEvent(
                externalNewEventId(), offer.getOfferId(), participantId, offer.getTrainingId(), discountCode, offer.getTrainingPrice().amount(), randomAmount(), offer.getTrainingPrice().currencyCode());
        testListener.willReturnDiscountCodeUsedEventAfterUseDiscountCodeCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance discountCodeReturned(String discountCode) {
        DiscountCodeReturnedEvent event = new DiscountCodeReturnedEvent(newEventId(), offer.getOfferId(), participantId, discountCode);
        testListener.willReturnAfterReturnDiscountCodeCommand(offer.getOfferId(), event);
        return this;
    }

    GivenOfferAcceptance bookableTraining() {
        OfferAcceptanceSagaEvent event = new TrainingPlaceBookedEvent(newEventId(), offer.getOfferId(), offer.getTrainingId(), participantId);
        testListener.willReturnAfterBookTrainingPlaceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance nonBookableTraining() {
        OfferAcceptanceSagaEvent event = new NoAvailableTrainingPlacesLeftEvent(newEventId(), offer.getOfferId(), offer.getTrainingId(), participantId);
        testListener.willReturnAfterBookTrainingPlaceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance trainingPriceNotChanged() {
        TrainingPriceNotChangedEvent event = new TrainingPriceNotChangedEvent(externalNewEventId(), offer.getOfferId(), offer.getTrainingId());
        testListener.willReturnTrainingPriceNotChangedEventAfterConfirmTrainingPriceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance trainingPriceChanged(BigDecimal price, String currency) {
        TrainingPriceChangedEvent event = new TrainingPriceChangedEvent(externalNewEventId(), offer.getOfferId(), offer.getTrainingId(), price, currency);
        testListener.willReturnTrainingPriceChangedEventAfterConfirmTrainingPriceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance alreadyRegisteredPersonFound() {
        AlreadyRegisteredPersonFoundEvent event = new AlreadyRegisteredPersonFoundEvent(externalNewEventId(), offer.getOfferId(), participantId);
        testListener.willReturnAlreadyRegisteredPersonFoundEventAfterRegisterPersonCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance personRegistered() {
        PersonRegisteredEvent event = new PersonRegisteredEvent(externalNewEventId(), offer.getOfferId(), participantId);
        testListener.willReturnPersonRegisteredEventAfterRegisterPersonCommand(offer.getOfferId(), event);

        return this;
    }

    private com.smalaca.schemaregistry.metadata.EventId externalNewEventId() {
        return com.smalaca.schemaregistry.metadata.EventId.newEventId();
    }


    OfferTestDto getOffer() {
        return offer;
    }
}
