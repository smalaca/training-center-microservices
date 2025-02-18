package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.AvailableOfferException;
import com.smalaca.opentrainings.domain.offer.DiscountException;
import com.smalaca.opentrainings.domain.offer.GivenOffer;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.NoAvailablePlacesException;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferAssertion;
import com.smalaca.opentrainings.domain.offer.OfferInFinalStateException;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion.assertThatOfferAcceptedEvent;
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion.assertThatOfferRejectedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
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
    private static final Price TRAINING_PRICE = randomPrice();
    private static final String NO_DISCOUNT_CODE = null;
    private static final String DISCOUNT_CODE = UUID.randomUUID().toString();
    private static final int NO_AVAILABLE_PLACES = 0;

    private final OfferRepository offerRepository = mock(OfferRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingOfferCatalogue trainingOfferCatalogue = mock(TrainingOfferCatalogue.class);
    private final DiscountService discountService = mock(DiscountService.class);
    private final Clock clock = mock(Clock.class);
    private final OfferApplicationService service = new OfferApplicationServiceFactory().offerApplicationService(
            offerRepository, eventRegistry, trainingOfferCatalogue, discountService, clock);

    private final GivenOfferFactory given = GivenOfferFactory.create(offerRepository);

    @BeforeEach
    void givenNow() {
        given(clock.now()).willReturn(now());
    }

    @Test
    void shouldInterruptTrainingChoiceIfThereNoAvailablePlaces() {
        givenTraining(new TrainingDto(NO_AVAILABLE_PLACES, TRAINING_PRICE));

        NoAvailablePlacesException actual = assertThrows(NoAvailablePlacesException.class, () -> service.chooseTraining(TRAINING_ID));

        assertThat(actual.getTrainingId()).isEqualTo(TRAINING_ID);
        then(offerRepository).should(never()).save(any());
    }

    @Test
    void shouldChooseTraining() {
        LocalDateTime creationDateTime = LocalDateTime.of(LocalDate.of(2024, 11, 1), LocalTime.now());
        given(clock.now()).willReturn(creationDateTime);
        givenAvailableTraining();

        service.chooseTraining(TRAINING_ID);

        thenOfferSaved()
                .isInitiated()
                .hasOfferNumberStartingWith("OFR/2024/11/")
                .hasCreationDateTime(creationDateTime)
                .hasTrainingId(TRAINING_ID)
                .hasTrainingPrice(TRAINING_PRICE);
    }

    @Test
    void shouldReturnOfferIdWhenTrainingChosen() {
        givenAvailableTraining();
        given(offerRepository.save(any())).willReturn(OFFER_ID);

        UUID actual = service.chooseTraining(TRAINING_ID);

        assertThat(actual).isEqualTo(OFFER_ID);
    }

    @Test
    void shouldInterruptOfferAcceptanceIfCannotUseDiscountCode() {
        givenInitiatedOffer();
        givenTrainingThatCanBeBooked();
        givenDiscount(DiscountResponse.failed("EXPIRED"));
        AcceptOfferCommand command = acceptOfferCommandWithDiscount(DISCOUNT_CODE);

        DiscountException actual = assertThrows(DiscountException.class, () -> service.accept(command));

        assertThat(actual).hasMessage("Discount Code could not be used because: EXPIRED");
        then(offerRepository).should(never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldRejectOfferWhenOfferExpiredAndTrainingPriceChanged(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();
        givenAvailableTrainingThatCanBeBookedWithPriceChanged();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferSaved().isRejected();
    }

    @Test
    void shouldPublishOfferRejectedEventWhenOfferExpiredAndTrainingPriceChanged() {
        givenOffer().createdMinutesAgo(42).initiated();
        givenAvailableTrainingThatCanBeBookedWithPriceChanged();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferRejectedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasReason("Offer expired");
    }

    @Test
    void shouldRejectOfferWhenTrainingNoLongerAvailable() {
        givenInitiatedOffer();
        givenTrainingThatCannotBeBooked();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferSaved().isRejected();
    }

    @Test
    void shouldPublishOfferRejectedEventWhenTrainingNoLongerAvailable() {
        givenInitiatedOffer();
        givenTrainingThatCannotBeBooked();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferRejectedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasReason("Training no longer available");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 10})
    void shouldAcceptOffer(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();
        givenTrainingThatCanBeBooked();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferSaved().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWhenOfferAccepted() {
        givenInitiatedOffer();
        givenTrainingThatCanBeBooked();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(TRAINING_PRICE)
                .hasNoDiscountCode();
    }

    @Test
    void shouldAcceptExpiredOfferWhenTrainingPriceDidNotChanged() {
        givenOffer().createdMinutesAgo(16).initiated();
        givenAvailableTrainingThatCanBeBookedWithSamePrice();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferSaved().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWhenExpiredOfferAndTrainingPriceDidNotChanged() {
        givenOffer().createdMinutesAgo(120).initiated();
        givenAvailableTrainingThatCanBeBookedWithSamePrice();

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(TRAINING_PRICE)
                .hasNoDiscountCode();
    }

    @Test
    void shouldAcceptOfferWithPriceAfterDiscount() {
        givenInitiatedOffer();
        givenTrainingThatCanBeBooked();
        givenDiscount(DiscountResponse.successful(randomPrice()));

        service.accept(acceptOfferCommandWithoutDiscount());

        thenOfferSaved().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWithPriceAfterDiscount() {
        givenInitiatedOffer();
        givenTrainingThatCanBeBooked();
        Price newPrice = randomPrice();
        givenDiscount(DiscountResponse.successful(newPrice));

        service.accept(acceptOfferCommandWithDiscount(DISCOUNT_CODE));

        thenOfferAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasFinalPrice(newPrice)
                .hasDiscountCode(DISCOUNT_CODE);
    }

    @Test
    void shouldInterruptDecliningIfOfferRejected() {
        givenOffer().rejected();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.decline(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptDecliningIfOfferAccepted() {
        givenOffer().accepted();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.decline(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already ACCEPTED");
    }

    @Test
    void shouldInterruptDecliningIfOfferTerminated() {
        givenOffer().createdMinutesAgo(42).terminated();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.decline(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already TERMINATED");
    }

    @Test
    void shouldDeclineOffer() {
        givenInitiatedOffer();

        service.decline(OFFER_ID);

        thenOfferSaved().isDeclined();
    }

    private void givenDiscount(DiscountResponse response) {
        DiscountCodeDto dto = new DiscountCodeDto(PARTICIPANT_ID, TRAINING_ID, TRAINING_PRICE, DISCOUNT_CODE);
        given(discountService.calculatePriceFor(dto)).willReturn(response);
    }

    @Test
    void shouldInterruptTerminationIfOfferRejected() {
        givenOffer().rejected();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.terminate(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already REJECTED");
    }

    @Test
    void shouldInterruptTerminationIfOfferAccepted() {
        givenOffer().accepted();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.terminate(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already ACCEPTED");
    }

    @Test
    void shouldInterruptTerminationIfOfferDeclined() {
        givenOffer().declined();

        OfferInFinalStateException actual = assertThrows(OfferInFinalStateException.class, () -> service.terminate(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " already DECLINED");
    }

    @Test
    void shouldInterruptTerminationIfOfferStillAvailable() {
        givenOffer().initiated();

        AvailableOfferException actual = assertThrows(AvailableOfferException.class, () -> service.terminate(OFFER_ID));

        assertThat(actual).hasMessage("Offer: " + OFFER_ID + " still available.");
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 13, 20})
    void shouldTerminateOffer(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.terminate(OFFER_ID);

        thenOfferSaved().isTerminated();
    }

    private void givenAvailableTrainingThatCanBeBookedWithPriceChanged() {
        givenAvailableTrainingWith(randomPrice());
        givenTrainingThatCanBeBooked();
    }

    private void givenAvailableTrainingThatCanBeBookedWithSamePrice() {
        givenAvailableTraining();
        givenTrainingThatCanBeBooked();
    }

    private void givenAvailableTraining() {
        givenAvailableTrainingWith(TRAINING_PRICE);
    }

    private void givenAvailableTrainingWith(Price price) {
        givenTraining(new TrainingDto(randomAvailability(), price));
    }

    private void givenTraining(TrainingDto trainingDto) {
        given(trainingOfferCatalogue.detailsOf(TRAINING_ID)).willReturn(trainingDto);
    }

    private int randomAvailability() {
        return FAKER.number().numberBetween(1, 42);
    }

    private void givenTrainingThatCanBeBooked() {
        givenTrainingToBookWith(TrainingBookingResponse.successful(TRAINING_ID, PARTICIPANT_ID));
    }

    private void givenTrainingThatCannotBeBooked() {
        givenTrainingToBookWith(TrainingBookingResponse.failed(TRAINING_ID, PARTICIPANT_ID));
    }

    private void givenTrainingToBookWith(TrainingBookingResponse response) {
        TrainingBookingDto dto = new TrainingBookingDto(TRAINING_ID, PARTICIPANT_ID);

        given(trainingOfferCatalogue.book(dto)).willReturn(response);
    }

    private OfferAssertion thenOfferSaved() {
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

    private AcceptOfferCommand acceptOfferCommandWithoutDiscount() {
        return acceptOfferCommandWithDiscount(NO_DISCOUNT_CODE);
    }

    private AcceptOfferCommand acceptOfferCommandWithDiscount(String discountCode) {
        return AcceptOfferCommand.nextAfter(randomPersonRegisteredEvent(), discountCode);
    }

    private PersonRegisteredEvent randomPersonRegisteredEvent() {
        return new PersonRegisteredEvent(newEventId(), OFFER_ID, PARTICIPANT_ID);
    }

    private void givenInitiatedOffer() {
        givenOffer().initiated();
    }

    private GivenOffer givenOffer() {
        return given.offer(OFFER_ID).trainingId(TRAINING_ID).trainingPrice(TRAINING_PRICE);
    }
}