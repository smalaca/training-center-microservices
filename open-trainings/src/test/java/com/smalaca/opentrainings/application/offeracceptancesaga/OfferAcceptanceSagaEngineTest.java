package com.smalaca.opentrainings.application.offeracceptancesaga;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.commandregistry.CommandRegistry;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaRepository;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.price.Price;
import net.datafaker.Faker;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent.offerAcceptedEventBuilder;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaAssertion.assertThatOfferAcceptanceSaga;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommandAssertion.assertThatAcceptOfferCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommandAssertion.assertThatBeginOfferAcceptanceCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BookTrainingPlaceCommandAssertion.assertThatBookTrainingPlaceCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommandAssertion.assertThatConfirmTrainingPriceCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommandAssertion.assertThatRegisterPersonCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommandAssertion.assertThatRejectOfferCommand;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.UseDiscountCodeCommandAssertion.assertThatUseDiscountCodeCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class OfferAcceptanceSagaEngineTest {
    private static final Faker FAKER = new Faker();
    private static final String DISCOUNT_CODE = FAKER.code().imei();
    private static final UUID OFFER_ID = randomId();
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final BigDecimal TRAINING_PRICE_AMOUNT = randomAmount();
    private static final String TRAINING_PRICE_CURRENCY_CODE = randomCurrency();
    private static final BigDecimal NEW_TRAINING_PRICE_AMOUNT = BigDecimal.valueOf(123.45);
    private static final String NEW_TRAINING_PRICE_CURRENCY_CODE = "USD";

    private final Clock clock = mock(Clock.class);
    private final OfferAcceptanceSagaRepository repository = mock(OfferAcceptanceSagaRepository.class);
    private final CommandRegistry registry = mock(CommandRegistry.class);
    private final OfferAcceptanceSagaEngine engine = new OfferAcceptanceSagaEngine(clock, repository, registry);

    @Test
    void shouldCreateOfferAcceptanceSagaWhenOfferAcceptanceRequestedEventAccepted() {
        givenNowSecondsAgo(1);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenOfferAcceptanceSagaSaved()
                .isInProgress()
                .hasOfferId(event.offerId())
                .hasDiscountCode(DISCOUNT_CODE)
                .consumedEvents(1)
                .consumedEventAt(event, NOW.minusSeconds(1));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldCreateOfferAcceptanceSagaWhenOfferAcceptanceRequestedEventAcceptedWithNoDiscountCode(String noDiscountCode) {
        givenNowSecondsAgo(1);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEventWith(noDiscountCode);

        engine.accept(event);

        thenOfferAcceptanceSagaSaved()
                .isInProgress()
                .hasOfferId(event.offerId())
                .hasNoDiscountCode()
                .consumedEvents(1)
                .consumedEventAt(event, NOW.minusSeconds(1));
    }

    @Test
    void shouldPublishCommandsWhenOfferAcceptanceRequestedEventAccepted() {
        givenNowSecondsAgo(1);
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(RegisterPersonCommand.class);
                    assertThatRegisterPersonCommand((RegisterPersonCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasFirstName(event.firstName())
                            .hasLastName(event.lastName())
                            .hasEmail(event.email())
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BeginOfferAcceptanceCommand.class);
                    assertThatBeginOfferAcceptanceCommand((BeginOfferAcceptanceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldHasParticipantIdWhenPersonRegisteredEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        PersonRegisteredEvent personRegisteredEvent = randomPersonRegisteredEvent();

        engine.accept(personRegisteredEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasParticipantId(PARTICIPANT_ID)
                .isOfferPriceNotConfirmed()
                .consumedEvents(2)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(personRegisteredEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishNoCommandWhenPersonRegisteredEventAcceptedAndOfferPriceNotConfirmed() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomPersonRegisteredEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenPersonRegisteredEventAcceptedAndOfferPriceConfirmed() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        PersonRegisteredEvent event = randomPersonRegisteredEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenPersonRegisteredEventAcceptedAndOfferPriceConfirmedAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        PersonRegisteredEvent event = randomPersonRegisteredEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldHasParticipantIdWhenAlreadyRegisteredPersonFoundEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        AlreadyRegisteredPersonFoundEvent alreadyRegisteredPersonFoundEvent = randomAlreadyRegisteredPersonFoundEvent();

        engine.accept(alreadyRegisteredPersonFoundEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasParticipantId(PARTICIPANT_ID)
                .isOfferPriceNotConfirmed()
                .consumedEvents(2)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(alreadyRegisteredPersonFoundEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishNoCommandWhenAlreadyRegisteredPersonFoundEventAcceptedAndOfferPriceNotConfirmed() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomAlreadyRegisteredPersonFoundEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenAlreadyRegisteredPersonFoundEventAcceptedAndOfferPriceConfirmed() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        AlreadyRegisteredPersonFoundEvent event = randomAlreadyRegisteredPersonFoundEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenAlreadyRegisteredPersonFoundEventAcceptedAndOfferPriceConfirmedAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        AlreadyRegisteredPersonFoundEvent event = randomAlreadyRegisteredPersonFoundEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldConfirmOfferPriceWhenUnexpiredOfferAcceptanceRequestedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();

        engine.accept(unexpiredOfferAcceptanceRequestedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceConfirmed()
                .hasTrainingId(TRAINING_ID)
                .hasTrainingPrice(Price.of(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE))
                .consumedEvents(2)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishNoCommandWhenUnexpiredOfferAcceptanceRequestedEventAcceptedAndHasNoParticipantId() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomUnexpiredOfferAcceptanceRequestedEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenUnexpiredOfferAcceptanceRequestedEventAcceptedAndPersonRegistered() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        UnexpiredOfferAcceptanceRequestedEvent event = randomUnexpiredOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenUnexpiredOfferAcceptanceRequestedEventAcceptedAndPersonRegisteredAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        UnexpiredOfferAcceptanceRequestedEvent event = randomUnexpiredOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenUnexpiredOfferAcceptanceRequestedEventAcceptedAndPersonAlreadyRegistered() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        UnexpiredOfferAcceptanceRequestedEvent event = randomUnexpiredOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenUnexpiredOfferAcceptanceRequestedEventAcceptedAndPersonAlreadyRegisteredAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        UnexpiredOfferAcceptanceRequestedEvent event = randomUnexpiredOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldAcceptExpiredOfferAcceptanceRequestedEvent() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        ExpiredOfferAcceptanceRequestedEvent expiredOfferAcceptanceRequestedEvent = randomExpiredOfferAcceptanceRequestedEvent();

        engine.accept(expiredOfferAcceptanceRequestedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceNotConfirmed()
                .hasTrainingId(TRAINING_ID)
                .hasTrainingPrice(Price.of(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE))
                .consumedEvents(2)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(expiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishConfirmTrainingPriceCommandWhenExpiredOfferAcceptanceRequestedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        ExpiredOfferAcceptanceRequestedEvent event = randomExpiredOfferAcceptanceRequestedEvent();

        engine.accept(event);

        thenPublishedCommands(1)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(ConfirmTrainingPriceCommand.class);
                    assertThatConfirmTrainingPriceCommand((ConfirmTrainingPriceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldConfirmOfferPriceWhenTrainingPriceNotChangedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        ExpiredOfferAcceptanceRequestedEvent expiredOfferAcceptanceRequestedEvent = randomExpiredOfferAcceptanceRequestedEvent();
        saga.accept(expiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceNotChangedEvent trainingPriceNotChangedEvent = randomTrainingPriceNotChangedEvent();

        engine.accept(trainingPriceNotChangedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceConfirmed()
                .consumedEvents(3)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(expiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(trainingPriceNotChangedEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishNoCommandWhenTrainingPriceNotChangedEventAcceptedAndHasNoParticipantId() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomTrainingPriceNotChangedEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenTrainingPriceNotChangedEventAcceptedAndPersonRegistered() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceNotChangedEvent event = randomTrainingPriceNotChangedEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenTrainingPriceNotChangedEventAcceptedAndPersonRegisteredAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceNotChangedEvent event = randomTrainingPriceNotChangedEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldPublishBookTrainingPlaceAndUseDiscountCodeCommandsWhenTrainingPriceNotChangedEventAcceptedAndPersonAlreadyRegistered() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceNotChangedEvent event = randomTrainingPriceNotChangedEvent();

        engine.accept(event);

        thenPublishedCommands(3)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                })
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(UseDiscountCodeCommand.class);
                    assertThatUseDiscountCodeCommand((UseDiscountCodeCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .hasTrainingPrice(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .isNextAfter(event.eventId());
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldPublishBookTrainingPlaceCommandWhenTrainingPriceNotChangedEventAcceptedAndPersonAlreadyRegisteredAndNoDiscountCode(String noDiscountCode) {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEventWith(noDiscountCode), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceNotChangedEvent event = randomTrainingPriceNotChangedEvent();

        engine.accept(event);

        thenPublishedCommands(2)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(BookTrainingPlaceCommand.class);
                    assertThatBookTrainingPlaceCommand((BookTrainingPlaceCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .hasTrainingId(TRAINING_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldSetDiscountCodeAlreadyUsedWhenDiscountCodeAlreadyUsedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        PersonRegisteredEvent personRegisteredEvent = randomPersonRegisteredEvent();
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        saga.accept(personRegisteredEvent, givenNowSecondsAgo(10));
        saga.accept(unexpiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        DiscountCodeAlreadyUsedEvent discountCodeAlreadyUsedEvent = randomDiscountCodeAlreadyUsedEvent();

        engine.accept(discountCodeAlreadyUsedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasDiscountCodeAlreadyUsed()
                .consumedEvents(4)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(personRegisteredEvent, NOW.minusSeconds(10))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(discountCodeAlreadyUsedEvent, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishNoCommandWhenTrainingPlacesNotBookedAndDiscountCodeAlreadyUsedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomDiscountCodeAlreadyUsedEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldPublishAcceptOfferCommandWhenTrainingPlaceBookedAndDiscountCodeAlreadyUsedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomPersonRegisteredEvent(), givenNowSecondsAgo(10));
        saga.accept(randomUnexpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        saga.accept(randomTrainingPlaceBookedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        DiscountCodeAlreadyUsedEvent event = randomDiscountCodeAlreadyUsedEvent();

        engine.accept(event);

        thenPublishedCommands(1)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(AcceptOfferCommand.class);
                    assertThatAcceptOfferCommand((AcceptOfferCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasDiscountCode(DISCOUNT_CODE)
                            .hasOfferId(OFFER_ID)
                            .hasParticipantId(PARTICIPANT_ID)
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldSetDiscountCodeUsedWhenDiscountCodeUsedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        PersonRegisteredEvent personRegisteredEvent = randomPersonRegisteredEvent();
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        saga.accept(personRegisteredEvent, givenNowSecondsAgo(10));
        saga.accept(unexpiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        DiscountCodeUsedEvent discountCodeUsedEvent = randomDiscountCodeUsedEvent();

        engine.accept(discountCodeUsedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasDiscountCodeUsed()
                .consumedEvents(4)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(personRegisteredEvent, NOW.minusSeconds(10))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(discountCodeUsedEvent, NOW.minusSeconds(5));
    }

    // to change, missing logic
    @Test
    void shouldPublishNoCommandWhenDiscountCodeUsedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomDiscountCodeUsedEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldSetTrainingPlaceBookedWhenTrainingPlaceBookedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        PersonRegisteredEvent personRegisteredEvent = randomPersonRegisteredEvent();
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        saga.accept(personRegisteredEvent, givenNowSecondsAgo(10));
        saga.accept(unexpiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPlaceBookedEvent trainingPlaceBookedEvent = randomTrainingPlaceBookedEvent();

        engine.accept(trainingPlaceBookedEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasTrainingPlaceBooked()
                .consumedEvents(4)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(personRegisteredEvent, NOW.minusSeconds(10))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(trainingPlaceBookedEvent, NOW.minusSeconds(5));
    }

    // to change, missing logic
    @Test
    void shouldPublishNoCommandWhenTrainingPlaceBookedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomTrainingPlaceBookedEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldSetHasNoAvailableTrainingPlacesLeftWhenNoAvailableTrainingPlacesLeftEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        PersonRegisteredEvent personRegisteredEvent = randomPersonRegisteredEvent();
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        saga.accept(personRegisteredEvent, givenNowSecondsAgo(10));
        saga.accept(unexpiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        NoAvailableTrainingPlacesLeftEvent noAvailableTrainingPlacesLeftEvent = randomNoAvailableTrainingPlacesLeftEvent();

        engine.accept(noAvailableTrainingPlacesLeftEvent);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoAvailableTrainingPlacesLeft()
                .consumedEvents(4)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(personRegisteredEvent, NOW.minusSeconds(10))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(noAvailableTrainingPlacesLeftEvent, NOW.minusSeconds(5));
    }

    // to change, missing logic
    @Test
    void shouldPublishNoCommandWhenNoAvailableTrainingPlacesLeftEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);

        engine.accept(randomNoAvailableTrainingPlacesLeftEvent());

        thenPublishedCommands(0);
    }

    @Test
    void shouldLeaveOfferPriceNotConfirmedWhenTrainingPriceChangedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        ExpiredOfferAcceptanceRequestedEvent expiredOfferAcceptanceRequestedEvent = randomExpiredOfferAcceptanceRequestedEvent();
        saga.accept(expiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceChangedEvent event = randomTrainingPriceChangedEvent();

        engine.accept(event);

        assertThatOfferAcceptanceSaga(saga)
                .isInProgress()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceNotConfirmed()
                .consumedEvents(3)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(expiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(8))
                .consumedEventAt(event, NOW.minusSeconds(5));
    }

    @Test
    void shouldPublishRejectOfferCommandWhenTrainingPriceChangedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        saga.accept(randomOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(13));
        saga.accept(randomAlreadyRegisteredPersonFoundEvent(), givenNowSecondsAgo(10));
        saga.accept(randomExpiredOfferAcceptanceRequestedEvent(), givenNowSecondsAgo(8));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        TrainingPriceChangedEvent event = randomTrainingPriceChangedEvent();

        engine.accept(event);

        thenPublishedCommands(1)
                .anySatisfy(actual -> {
                    assertThat(actual).isInstanceOf(RejectOfferCommand.class);
                    assertThatRejectOfferCommand((RejectOfferCommand) actual)
                            .hasOfferId(OFFER_ID)
                            .hasReason("Training price changed to: 123.45 USD")
                            .isNextAfter(event.eventId());
                });
    }

    @Test
    void shouldRejectWhenNotAvailableOfferAcceptanceRequestedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        NotAvailableOfferAcceptanceRequestedEvent event = randomNotAvailableOfferAcceptanceRequestedEvent();

        engine.accept(event);

        assertThatOfferAcceptanceSaga(saga)
                .isRejected()
                .hasRejectionReason("Offer already " + event.status())
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceNotConfirmed()
                .consumedEvents(2)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(event, NOW.minusSeconds(5));
    }

    @Test
    void shouldRejectWhenOfferRejectedEventAccepted() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        ExpiredOfferAcceptanceRequestedEvent expiredOfferAcceptanceRequestedEvent = randomExpiredOfferAcceptanceRequestedEvent();
        saga.accept(expiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(9));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(5);
        OfferRejectedEvent event = randomOfferRejectedEvent();

        engine.accept(event);

        assertThatOfferAcceptanceSaga(saga)
                .isRejected()
                .hasRejectionReason(event.reason())
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasNoParticipantId()
                .isOfferPriceNotConfirmed()
                .consumedEvents(3)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(expiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(9))
                .consumedEventAt(event, NOW.minusSeconds(5));
    }

    // to change, missing events
    @Test
    void shouldAcceptWhenOfferAcceptedEventConsumed() {
        OfferAcceptanceSaga saga = new OfferAcceptanceSaga(OFFER_ID);
        OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent = randomOfferAcceptanceRequestedEvent();
        saga.accept(offerAcceptanceRequestedEvent, givenNowSecondsAgo(13));
        AlreadyRegisteredPersonFoundEvent randomAlreadyRegisteredPersonFoundEvent = randomAlreadyRegisteredPersonFoundEvent();
        saga.accept(randomAlreadyRegisteredPersonFoundEvent, givenNowSecondsAgo(10));
        UnexpiredOfferAcceptanceRequestedEvent unexpiredOfferAcceptanceRequestedEvent = randomUnexpiredOfferAcceptanceRequestedEvent();
        saga.accept(unexpiredOfferAcceptanceRequestedEvent, givenNowSecondsAgo(5));
        given(repository.findById(OFFER_ID)).willReturn(saga);
        givenNowSecondsAgo(3);
        OfferAcceptedEvent event = randomOfferAcceptedEvent();

        engine.accept(event);

        assertThatOfferAcceptanceSaga(saga)
                .isAccepted()
                .hasNoRejectionReason()
                .hasOfferId(OFFER_ID)
                .hasDiscountCode(DISCOUNT_CODE)
                .hasParticipantId(PARTICIPANT_ID)
                .isOfferPriceConfirmed()
                .consumedEvents(4)
                .consumedEventAt(offerAcceptanceRequestedEvent, NOW.minusSeconds(13))
                .consumedEventAt(randomAlreadyRegisteredPersonFoundEvent, NOW.minusSeconds(10))
                .consumedEventAt(unexpiredOfferAcceptanceRequestedEvent, NOW.minusSeconds(5))
                .consumedEventAt(event, NOW.minusSeconds(3));
    }

    private Clock givenNowSecondsAgo(int seconds) {
        given(clock.now()).willReturn(NOW.minusSeconds(seconds));
        return clock;
    }

    private ListAssert<OfferAcceptanceSagaCommand> thenPublishedCommands(int publishedCommands) {
        ArgumentCaptor<OfferAcceptanceSagaCommand> captor = ArgumentCaptor.forClass(OfferAcceptanceSagaCommand.class);
        then(registry).should(times(publishedCommands)).publish(captor.capture());

        return assertThat(captor.getAllValues());
    }

    private OfferAcceptanceSagaAssertion thenOfferAcceptanceSagaSaved() {
        ArgumentCaptor<OfferAcceptanceSaga> captor = ArgumentCaptor.forClass(OfferAcceptanceSaga.class);
        then(repository).should().save(captor.capture());

        return assertThatOfferAcceptanceSaga(captor.getValue());
    }

    private NotAvailableOfferAcceptanceRequestedEvent randomNotAvailableOfferAcceptanceRequestedEvent() {
        return NotAvailableOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), FAKER.lorem().sentence());
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return randomOfferAcceptanceRequestedEventWith(DISCOUNT_CODE);
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEventWith(String discountCode) {
        return OfferAcceptanceRequestedEvent.create(OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), discountCode);
    }

    private PersonRegisteredEvent randomPersonRegisteredEvent() {
        return new PersonRegisteredEvent(newEventId(), OFFER_ID, PARTICIPANT_ID);
    }

    private AlreadyRegisteredPersonFoundEvent randomAlreadyRegisteredPersonFoundEvent() {
        return new AlreadyRegisteredPersonFoundEvent(newEventId(), OFFER_ID, PARTICIPANT_ID);
    }

    private OfferAcceptedEvent randomOfferAcceptedEvent() {
        AcceptOfferCommand command = AcceptOfferCommand.nextAfter(randomTrainingPriceNotChangedEvent(), PARTICIPANT_ID, DISCOUNT_CODE);

        return offerAcceptedEventBuilder()
                .nextAfter(command)
                .withOfferId(OFFER_ID)
                .build();
    }

    private UnexpiredOfferAcceptanceRequestedEvent randomUnexpiredOfferAcceptanceRequestedEvent() {
        Price trainingPrice = Price.of(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE);
        return UnexpiredOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), TRAINING_ID, trainingPrice);
    }

    private BeginOfferAcceptanceCommand randomBeginOfferAcceptanceCommand() {
        return BeginOfferAcceptanceCommand.nextAfter(randomOfferAcceptanceRequestedEvent());
    }

    private OfferRejectedEvent randomOfferRejectedEvent() {
        RejectOfferCommand command = RejectOfferCommand.nextAfter(randomTrainingPriceChangedEvent());
        return OfferRejectedEvent.nextAfter(command);
    }

    private ExpiredOfferAcceptanceRequestedEvent randomExpiredOfferAcceptanceRequestedEvent() {
        Price trainingPrice = Price.of(TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE);
        return ExpiredOfferAcceptanceRequestedEvent.nextAfter(randomBeginOfferAcceptanceCommand(), TRAINING_ID, trainingPrice);
    }

    private TrainingPriceNotChangedEvent randomTrainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), OFFER_ID, TRAINING_ID);
    }

    private TrainingPriceChangedEvent randomTrainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(newEventId(), OFFER_ID, TRAINING_ID, NEW_TRAINING_PRICE_AMOUNT, NEW_TRAINING_PRICE_CURRENCY_CODE);
    }

    private DiscountCodeAlreadyUsedEvent randomDiscountCodeAlreadyUsedEvent() {
        return new DiscountCodeAlreadyUsedEvent(newEventId(), OFFER_ID, PARTICIPANT_ID, TRAINING_ID, DISCOUNT_CODE);
    }

    private DiscountCodeUsedEvent randomDiscountCodeUsedEvent() {
        return new DiscountCodeUsedEvent(newEventId(), OFFER_ID, PARTICIPANT_ID, TRAINING_ID, DISCOUNT_CODE, TRAINING_PRICE_AMOUNT, NEW_TRAINING_PRICE_AMOUNT, TRAINING_PRICE_CURRENCY_CODE);
    }

    private TrainingPlaceBookedEvent randomTrainingPlaceBookedEvent() {
        return new TrainingPlaceBookedEvent(newEventId(), OFFER_ID, PARTICIPANT_ID, TRAINING_ID);
    }

    private NoAvailableTrainingPlacesLeftEvent randomNoAvailableTrainingPlacesLeftEvent() {
        return new NoAvailableTrainingPlacesLeftEvent(newEventId(), OFFER_ID, PARTICIPANT_ID, TRAINING_ID);
    }
}