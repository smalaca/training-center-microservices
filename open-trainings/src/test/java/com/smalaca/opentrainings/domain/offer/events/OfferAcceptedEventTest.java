package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion.assertThatOfferAcceptedEvent;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;

class OfferAcceptedEventTest {
    private static final Faker FAKER = new Faker();
    private static final String DISCOUNT_CODE = FAKER.code().ean13();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final Price TRAINING_PRICE = randomPrice();
    private static final Price FINAL_PRICE = randomPrice();
    private static final UUID OFFER_ID = randomId();

    @Test
    void shouldCreateOfferAcceptedEvent() {
        AcceptOfferCommand command = acceptOfferCommand();
        OfferAcceptedEvent actual = OfferAcceptedEvent.offerAcceptedEventBuilder()
                .nextAfter(command)
                .withOfferId(OFFER_ID)
                .withTrainingId(TRAINING_ID)
                .withParticipantId(PARTICIPANT_ID)
                .withTrainingPrice(TRAINING_PRICE)
                .withFinalPrice(FINAL_PRICE)
                .withDiscountCode(DISCOUNT_CODE)
                .build();

        assertThatOfferAcceptedEvent(actual)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(FINAL_PRICE)
                .hasDiscountCode(DISCOUNT_CODE)
                .isNextAfter(command.commandId());
    }

    private AcceptOfferCommand acceptOfferCommand() {
        OfferAcceptanceSagaEvent event = trainingPriceNotChangedEvent();

        return acceptOfferCommandBuilder(event, PARTICIPANT_ID)
                .withDiscountCodeUsed(DISCOUNT_CODE)
                .withFinalPrice(FINAL_PRICE)
                .build();
    }

    private TrainingPriceNotChangedEvent trainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), OFFER_ID, TRAINING_ID);
    }
}