package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.schemaregistry.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.schemaregistry.offeracceptancesaga.events.TrainingPriceNotChangedEvent;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomId;

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
        com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent event = new com.smalaca.schemaregistry.offeracceptancesaga.events.DiscountCodeReturnedEvent(externalNewEventId(), offer.getOfferId(), participantId, discountCode);
        testListener.willReturnAfterReturnDiscountCodeCommand(offer.getOfferId(), event);
        return this;
    }

    GivenOfferAcceptance bookableTraining() {
        TrainingPlaceBookedEvent event = new TrainingPlaceBookedEvent(externalNewEventId(), offer.getOfferId(), participantId, offer.getTrainingId());
        testListener.willReturnTrainingPlaceBookedEventAfterBookTrainingPlaceCommand(offer.getOfferId(), event);

        return this;
    }

    GivenOfferAcceptance nonBookableTraining() {
        NoAvailableTrainingPlacesLeftEvent event = new NoAvailableTrainingPlacesLeftEvent(externalNewEventId(), offer.getOfferId(), participantId, offer.getTrainingId());
        testListener.willReturnNoAvailableTrainingPlacesLeftEventAfterBookTrainingPlaceCommand(offer.getOfferId(), event);

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
