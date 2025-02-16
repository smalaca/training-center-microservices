package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion.assertThatOfferAcceptedEvent;
import static java.time.LocalDateTime.now;

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
         CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
         AcceptOfferCommand command = new AcceptOfferCommand(commandId, OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), DISCOUNT_CODE);

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
                 .isNextAfter(commandId);
     }
}