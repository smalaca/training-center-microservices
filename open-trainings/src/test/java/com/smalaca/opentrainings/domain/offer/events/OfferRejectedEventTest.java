package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion.assertThatOfferRejectedEvent;
import static java.time.LocalDateTime.now;

class OfferRejectedEventTest {
    private static final Faker FAKER = new Faker();

    @Test
    void shouldCreateOfferRejectedEvent() {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        RejectOfferCommand command = new RejectOfferCommand(commandId, randomId(), FAKER.lorem().sentence());

        OfferRejectedEvent actual = OfferRejectedEvent.nextAfter(command);

        assertThatOfferRejectedEvent(actual)
                .hasOfferId(command.offerId())
                .hasReason(command.reason())
                .isNextAfter(commandId);
    }
}