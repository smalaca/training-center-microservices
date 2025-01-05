package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.GivenOffer;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.MissingParticipantException;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferAssertion;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataRequest;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion.assertThatOfferAcceptedEvent;
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion.assertThatOfferRejectedEvent;
import static com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse.failed;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class OfferApplicationServiceTest {
    private static final Faker FAKER = new Faker();

    private static final UUID OFFER_ID = randomId();
    private static final UUID TRAINING_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();
    private static final BigDecimal AMOUNT = randomAmount();
    private static final String CURRENCY = randomCurrency();
    private static final String FIRST_NAME = FAKER.name().firstName();
    private static final String LAST_NAME = FAKER.name().lastName();
    private static final String EMAIL = FAKER.internet().emailAddress();

    private final OfferRepository offerRepository = mock(OfferRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final PersonalDataManagement personalDataManagement = mock(PersonalDataManagement.class);
    private final TrainingOfferCatalogue trainingOfferCatalogue = mock(TrainingOfferCatalogue.class);
    private final Clock clock = mock(Clock.class);
    private final OfferApplicationService service = new OfferApplicationService(offerRepository, eventRegistry, personalDataManagement, trainingOfferCatalogue, clock);

    private final GivenOfferFactory given = GivenOfferFactory.create(offerRepository);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(now());
    }

    @Test
    void shouldInterruptOfferAcceptanceIfCouldNotRetrieveParticipantId() {
        givenInitiatedOffer();
        givenParticipant(failed());
        givenAvailableTraining();

        assertThrows(MissingParticipantException.class, () -> service.accept(acceptOfferCommand()));

        then(offerRepository).should(never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOfferWhenOfferExpiredAndTrainingPriceChanged(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();
        givenParticipant();
        givenAvailableTrainingWithPriceChanged();

        service.accept(acceptOfferCommand());

        thenOfferUpdated().isRejected();
    }

    @Test
    void shouldPublishOfferRejectedEventWhenOfferExpiredAndTrainingPriceChanged() {
        givenOffer().createdMinutesAgo(42).initiated();
        givenParticipant();
        givenAvailableTrainingWithPriceChanged();

        service.accept(acceptOfferCommand());

        thenOfferRejectedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasReason("Offer expired");
    }

    @Test
    void shouldRejectOfferWhenTrainingNoLongerAvailable() {
        givenInitiatedOffer();
        givenParticipant();
        givenNotAvailableTraining();

        service.accept(acceptOfferCommand());

        thenOfferUpdated().isRejected();
    }

    @Test
    void shouldPublishOfferRejectedEventWhenTrainingNoLongerAvailable() {
        givenInitiatedOffer();
        givenParticipant();
        givenNotAvailableTraining();

        service.accept(acceptOfferCommand());

        thenOfferRejectedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasReason("Training no longer available");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldAcceptOffer(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();
        givenParticipant();
        givenAvailableTraining();

        service.accept(acceptOfferCommand());

        thenOfferUpdated().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWhenOfferAccepted() {
        givenInitiatedOffer();
        givenParticipant();
        givenAvailableTraining();

        service.accept(acceptOfferCommand());

        thenOfferAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasPrice(AMOUNT, CURRENCY);
    }

    @Test
    void shouldAcceptExpiredOfferWhenTrainingPriceDidNotChanged() {
        givenOffer().createdMinutesAgo(16).initiated();
        givenParticipant();
        givenAvailableTrainingWithSamePrice();

        service.accept(acceptOfferCommand());

        thenOfferUpdated().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWhenExpiredOfferAndTrainingPriceDidNotChanged() {
        givenOffer().createdMinutesAgo(120).initiated();
        givenParticipant();
        givenAvailableTrainingWithSamePrice();

        service.accept(acceptOfferCommand());

        thenOfferAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasPrice(AMOUNT, CURRENCY);
    }

    private void givenAvailableTrainingWithPriceChanged() {
        BigDecimal newPrice = AMOUNT.add(randomAmount());
        given(trainingOfferCatalogue.priceFor(TRAINING_ID)).willReturn(Price.of(newPrice, CURRENCY));
        givenAvailableTraining();
    }

    private void givenAvailableTrainingWithSamePrice() {
        given(trainingOfferCatalogue.priceFor(TRAINING_ID)).willReturn(Price.of(AMOUNT, CURRENCY));
        givenAvailableTraining();
    }

    private void givenAvailableTraining() {
        givenTraining(TrainingBookingResponse.successful(TRAINING_ID, PARTICIPANT_ID));
    }

    private void givenNotAvailableTraining() {
        givenTraining(TrainingBookingResponse.failed(TRAINING_ID, PARTICIPANT_ID));
    }

    private void givenTraining(TrainingBookingResponse response) {
        TrainingBookingDto dto = new TrainingBookingDto(TRAINING_ID, PARTICIPANT_ID);

        given(trainingOfferCatalogue.book(dto)).willReturn(response);
    }

    private OfferAssertion thenOfferUpdated() {
        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        then(offerRepository).should().save(captor.capture());
        Offer actual = captor.getValue();

        return assertThatOffer(actual);
    }

    private OfferRejectedEventAssertion thenOfferRejectedEventPublished() {
        ArgumentCaptor<OfferRejectedEvent> captor = ArgumentCaptor.forClass(OfferRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OfferRejectedEvent actual = captor.getValue();

        return assertThatOfferRejectedEvent(actual);
    }

    private OfferAcceptedEventAssertion thenOfferAcceptedEventPublished() {
        ArgumentCaptor<OfferAcceptedEvent> captor = ArgumentCaptor.forClass(OfferAcceptedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OfferAcceptedEvent actual = captor.getValue();

        return assertThatOfferAcceptedEvent(actual);
    }

    private AcceptOfferCommand acceptOfferCommand() {
        return new AcceptOfferCommand(OFFER_ID, FIRST_NAME, LAST_NAME, EMAIL);
    }

    private void givenParticipant() {
        givenParticipant(PersonalDataResponse.successful(PARTICIPANT_ID));
    }

    private void givenParticipant(PersonalDataResponse response) {
        PersonalDataRequest request = PersonalDataRequest.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(EMAIL)
                .build();
        given(personalDataManagement.save(request)).willReturn(response);
    }

    private void givenInitiatedOffer() {
        givenOffer().initiated();
    }

    private GivenOffer givenOffer() {
        return given.offer(OFFER_ID).trainingId(TRAINING_ID).amount(AMOUNT).currency(CURRENCY).initiated();
    }
}