package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent.offerAcceptedEventBuilder;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion.assertThatOfferAcceptanceSaga;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommandAssertion.assertThatAcceptOfferCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommandAssertion.assertThatRegisterPersonCommand;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OfferAcceptanceSagaTest {
    private static final Faker FAKER = new Faker();
    private static final UUID OFFER_ID = randomId();
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    void shouldRecognizeCreatedOfferAcceptanceSagaAsNotCompleted() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);

        assertThatOfferAcceptanceSaga(actual).isInProgress();
    }

    @Test
    void shouldPublishRegisterPersonCommandWhenOfferAcceptanceRequestedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        RegisterPersonCommand actual = saga.accept(event, givenClock(5));

        assertThatRegisterPersonCommand(actual)
                .hasOfferId(OFFER_ID)
                .hasFirstName(event.firstName())
                .hasLastName(event.lastName())
                .hasEmail(event.email())
                .isNextAfter(event.eventId());
    }

    @Test
    void shouldRecognizeSagaAsInProgressWhenOfferAcceptanceRequestedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        saga.accept(event, givenClock(5));

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .consumedEvents(1)
                .consumedEventAt(event, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishAcceptOfferCommandWhenPersonRegisteredEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenClock(13));
        PersonRegisteredEvent event = randomPersonRegisteredEvent();

        AcceptOfferCommand actual = saga.accept(event, givenClock(5));

        assertThatAcceptOfferCommand(actual)
                .hasOfferId(OFFER_ID)
                .hasParticipantId(event.participantId())
                .hasDiscountCode(event.discountCode())
                .isNextAfter(event.eventId());
    }

    @Test
    void shouldRecognizeSagaAsInProgressWhenPersonRegisteredEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent();
        saga.accept(eventOne, givenClock(13));
        PersonRegisteredEvent eventTwo = randomPersonRegisteredEvent();

        saga.accept(eventTwo, givenClock(5));

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .consumedEvents(2)
                .consumedEventAt(eventOne, NOW.minusSeconds(13))
                .consumedEventAt(eventTwo, NOW.minusSeconds(5));
    }

    private PersonRegisteredEvent randomPersonRegisteredEvent() {
        return new PersonRegisteredEvent(randomEventId(), OFFER_ID, randomId(), FAKER.code().imei());
    }

    @Test
    void shouldRecognizeSagaAsInProgressWhenOfferAcceptanceRequested() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        actual.acceptToRemove(event, givenClock(5));

        assertThatOfferAcceptanceSaga(actual)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .consumedEvents(1)
                .consumedEventAt(event, NOW.minusSeconds(5));
    }

    @Test
    void shouldRecognizeSagaAsAcceptedWhenOfferAccepted() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent();
        actual.acceptToRemove(eventOne, givenClock(13));
        OfferAcceptedEvent eventTwo = randomOfferAcceptedEvent();

        actual.accept(eventTwo, givenClock(7));

        assertThatOfferAcceptanceSaga(actual)
                .isAccepted()
                .hasOfferId(OFFER_ID)
                .consumedEvents(2)
                .consumedEventAt(eventOne, NOW.minusSeconds(13))
                .consumedEventAt(eventTwo, NOW.minusSeconds(7));
    }

    @Test
    void shouldRecognizeSagaAsRejectedWhenOfferRejected() {
        OfferAcceptanceSaga actual = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent eventOne = randomOfferAcceptanceRequestedEvent();
        actual.acceptToRemove(eventOne, givenClock(13));
        OfferRejectedEvent eventTwo = randomOfferRejectedEvent();

        actual.accept(eventTwo, givenClock(7));

        assertThatOfferAcceptanceSaga(actual)
                .isRejected()
                .hasOfferId(OFFER_ID)
                .consumedEvents(2)
                .consumedEventAt(eventOne, NOW.minusSeconds(13))
                .consumedEventAt(eventTwo, NOW.minusSeconds(7));
    }

    private Clock givenClock(int seconds) {
        Clock clock = mock(Clock.class);
        given(clock.now()).willReturn(NOW.minusSeconds(seconds));
        return clock;
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().imei());
    }

    private OfferAcceptedEvent randomOfferAcceptedEvent() {
        AcceptOfferCommand command = AcceptOfferCommand.nextAfter(randomOfferAcceptanceRequestedEvent());

        return offerAcceptedEventBuilder()
                .nextAfter(command)
                .withOfferId(OFFER_ID)
                .build();
    }

    private OfferRejectedEvent randomOfferRejectedEvent() {
        RejectOfferCommand command = new RejectOfferCommand(randomCommandId(), OFFER_ID, FAKER.lorem().sentence());
        return OfferRejectedEvent.nextAfter(command);
    }

    private EventId randomEventId() {
        return new EventId(randomId(), randomId(), randomId(), NOW);
    }

    private CommandId randomCommandId() {
        return new CommandId(randomId(), randomId(), randomId(), NOW);
    }
}