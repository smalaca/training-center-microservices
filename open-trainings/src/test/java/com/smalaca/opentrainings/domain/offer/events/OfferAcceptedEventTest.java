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
    void shouldCreateOfferAcceptedEventWithoutDiscountCode() {
        AcceptOfferCommand command = acceptOfferCommand().build();
        OfferAcceptedEvent actual = OfferAcceptedEvent.nextAfter(command, TRAINING_ID, TRAINING_PRICE);

        assertThatOfferAcceptedEvent(actual)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasNoFinalPrice()
                .hasNoDiscountCode()
                .hasDiscountCodeNotUsed()
                .hasDiscountCodeNotAlreadyUsed()
                .isNextAfter(command.commandId());
    }

    @Test
    void shouldCreateOfferAcceptedEventWithDiscountCodeUsed() {
        AcceptOfferCommand command = acceptOfferCommand().withDiscountCodeUsed(DISCOUNT_CODE, FINAL_PRICE).build();
        OfferAcceptedEvent actual = OfferAcceptedEvent.nextAfter(command, TRAINING_ID, TRAINING_PRICE);

        assertThatOfferAcceptedEvent(actual)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(FINAL_PRICE)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasDiscountCodeUsed()
                .hasDiscountCodeNotAlreadyUsed()
                .isNextAfter(command.commandId());
    }
    @Test
    void shouldCreateOfferAcceptedEventWithDiscountCodeAlreadyUsed() {
        AcceptOfferCommand command = acceptOfferCommand().withDiscountCodeAlreadyUsed(DISCOUNT_CODE).build();
        OfferAcceptedEvent actual = OfferAcceptedEvent.nextAfter(command, TRAINING_ID, TRAINING_PRICE);

        assertThatOfferAcceptedEvent(actual)
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasNoFinalPrice()
                .hasDiscountCode(DISCOUNT_CODE)
                .hasDiscountCodeNotUsed()
                .hasDiscountCodeAlreadyUsed()
                .isNextAfter(command.commandId());
    }

    private AcceptOfferCommand.Builder acceptOfferCommand() {
        OfferAcceptanceSagaEvent event = trainingPriceNotChangedEvent();

        return acceptOfferCommandBuilder(event, PARTICIPANT_ID);
    }

    private TrainingPriceNotChangedEvent trainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), OFFER_ID, TRAINING_ID);
    }
}