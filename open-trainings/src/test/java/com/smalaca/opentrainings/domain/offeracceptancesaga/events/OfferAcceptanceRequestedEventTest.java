package com.smalaca.opentrainings.domain.offeracceptancesaga.events;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommandAssertion.assertThatAcceptOfferCommand;

class OfferAcceptanceRequestedEventTest {
    private static final Faker FAKER = new Faker();

    @Test
    void shouldCreateAcceptOfferCommand() {
        OfferAcceptanceRequestedEvent event = givenOfferAcceptanceRequestedEvent();

        AcceptOfferCommand actual = event.asAcceptOfferCommand();

        assertThatAcceptOfferCommand(actual)
                .hasOfferId(event.offerId())
                .hasFirstName(event.firstName())
                .hasLastName(event.lastName())
                .hasEmail(event.email())
                .hasDiscountCode(event.discountCode())
                .hasTraceIdSameAs(event)
                .hasCorrelationIdSameAs(event)
                .hasDifferentCommandIdThan(event)
                .hasCreationDateTimeAfterOrEqual(event);
    }

    private OfferAcceptanceRequestedEvent givenOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().ean8());
    }
}