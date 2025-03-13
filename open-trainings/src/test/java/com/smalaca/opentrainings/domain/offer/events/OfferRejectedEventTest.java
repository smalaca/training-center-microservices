package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion.assertThatOfferRejectedEvent;

class OfferRejectedEventTest {
    @Test
    void shouldCreateOfferRejectedEvent() {
        RejectOfferCommand command = RejectOfferCommand.nextAfter(trainingPriceChangedEvent());

        OfferRejectedEvent actual = OfferRejectedEvent.nextAfter(command);

        assertThatOfferRejectedEvent(actual)
                .hasOfferId(command.offerId())
                .hasReason(command.reason())
                .isNextAfter(command.commandId());
    }

    private TrainingPriceChangedEvent trainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(newEventId(), randomId(), randomId(), randomAmount(), randomCurrency());
    }
}