package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.AvailableOfferException;
import com.smalaca.opentrainings.domain.offer.GivenOffer;
import com.smalaca.opentrainings.domain.offer.GivenOfferFactory;
import com.smalaca.opentrainings.domain.offer.InvalidOfferStatusException;
import com.smalaca.opentrainings.domain.offer.NoAvailablePlacesException;
import com.smalaca.opentrainings.domain.offer.OfferAssertion;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.smalaca.opentrainings.data.Random.randomId;
import static com.smalaca.opentrainings.data.Random.randomPrice;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand.acceptOfferCommandBuilder;
import static java.math.BigDecimal.valueOf;
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
    private final Clock clock = mock(Clock.class);
    private final OfferApplicationService service = offerApplicationService(offerRepository);

    private final GivenOfferFactory given = GivenOfferFactory.create(offerRepository);
    private final OfferTestThen then = new OfferTestThen(eventRegistry, offerRepository);

    private OfferApplicationService offerApplicationService(OfferRepository repository) {
        return new OfferApplicationServiceFactory().offerApplicationService(repository, eventRegistry, trainingOfferCatalogue, clock);
    }

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

        then.offerSaved()
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

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 7, 10, 13, 20, 100})
    void shouldBeginOfferAcceptance(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();

        service.beginAcceptance(beginOfferAcceptanceCommand());

        then.offerSaved().isAcceptanceInProgress();
    }

    @Test
    void shouldPublishUnexpiredOfferAcceptanceRequestedEventWhenOfferNotExpired() {
        givenInitiatedOffer();
        BeginOfferAcceptanceCommand command = beginOfferAcceptanceCommand();

        service.beginAcceptance(command);

        then.unexpiredOfferAcceptanceRequestedEventPublished()
                .hasOfferId(OFFER_ID)
                .isNextAfter(command.commandId());
    }

    @Test
    void shouldPublishUnexpiredOfferAcceptanceRequestedEventWhenOfferAtBoundaryOfExpiration() {
        BeginOfferAcceptanceCommand command = beginOfferAcceptanceCommand();
        LocalDateTime createdAt = command.commandId().creationDateTime().minusMinutes(10);
        givenOffer().createdAt(createdAt).initiated();

        service.beginAcceptance(command);

        then.unexpiredOfferAcceptanceRequestedEventPublished()
                .hasOfferId(OFFER_ID)
                .isNextAfter(command.commandId());
    }

    @ParameterizedTest
    @ValueSource(ints = {13, 20, 100})
    void shouldPublishExpiredOfferAcceptanceRequestedEventWhenOfferExpired(int minutes) {
        givenOffer().createdMinutesAgo(minutes).initiated();
        BeginOfferAcceptanceCommand command = beginOfferAcceptanceCommand();

        service.beginAcceptance(command);

        then.expiredOfferAcceptanceRequestedEventPublished()
                .hasOfferId(OFFER_ID)
                .isNextAfter(command.commandId());
    }

    @ParameterizedTest
    @MethodSource("offersNotInInitiatedStatus")
    void shouldNotChangeStateOfOfferWhenBeginOfferAcceptanceCommandReceivedAndOfferInFinalState(GivenOffer offer, OfferRepository repository) {
        BeginOfferAcceptanceCommand command = beginOfferAcceptanceCommand();

        offerApplicationService(repository).beginAcceptance(command);

        thenOfferSaved(repository)
                .hasStatus(offer.getDto().getStatus());
    }

    @ParameterizedTest
    @MethodSource("offersNotInInitiatedStatus")
    void shouldPublishNotAvailableOfferAcceptanceRequestedEventWhenOfferInFinalState(GivenOffer offer, OfferRepository repository) {
        BeginOfferAcceptanceCommand command = beginOfferAcceptanceCommand();

        offerApplicationService(repository).beginAcceptance(command);

        then.notAvailableOfferAcceptanceRequestedEvent()
                .hasOfferId(offer.getDto().getOfferId())
                .isNextAfter(command.commandId());
    }

    @Test
    void shouldPublishOfferRejectedEventWhenTrainingNoLongerAvailable() {
        givenOfferWithAcceptanceInProgress();

        service.accept(acceptOfferCommand());

        then.offerSaved().isAccepted();
    }

    @Test
    void shouldPublishOfferAcceptedEventWhenOfferAccepted() {
        givenOfferWithAcceptanceInProgress();

        service.accept(acceptOfferCommand());

        then.offerAcceptedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasTrainingId(TRAINING_ID)
                .hasParticipantId(PARTICIPANT_ID)
                .hasTrainingPrice(TRAINING_PRICE)
                .hasDiscountCode(DISCOUNT_CODE);
    }

    @ParameterizedTest
    @MethodSource("offersNotInAcceptanceInProgressStatus")
    void shouldInterruptAcceptingOfferIfAcceptanceNotInProgress(GivenOffer offer, OfferRepository repository) {
        OfferTestDto dto = offer.getDto();
        Executable executable = () -> offerApplicationService(repository).accept(acceptOfferCommand());

        InvalidOfferStatusException actual = assertThrows(InvalidOfferStatusException.class, executable);

        assertThat(actual).hasMessage("Offer: " + dto.getOfferId() + " acceptance not in progress.");
    }

    @ParameterizedTest
    @MethodSource("offersNotInInitiatedStatus")
    void shouldInterruptDecliningOfferIfNotInInitiatedStatus(GivenOffer offer, OfferRepository repository) {
        OfferTestDto dto = offer.getDto();
        Executable executable = () -> offerApplicationService(repository).decline(dto.getOfferId());

        InvalidOfferStatusException actual = assertThrows(InvalidOfferStatusException.class, executable);

        assertThat(actual).hasMessage("Offer: " + dto.getOfferId() + " not in INITIATED status.");
    }

    @Test
    void shouldDeclineOffer() {
        givenInitiatedOffer();

        service.decline(OFFER_ID);

        then.offerSaved().isDeclined();
    }

    @ParameterizedTest
    @MethodSource("offersNotInInitiatedStatus")
    void shouldInterruptTerminationIfOfferNotInInitiatedStatus(GivenOffer offer, OfferRepository repository) {
        OfferTestDto dto = offer.getDto();
        Executable executable = () -> offerApplicationService(repository).terminate(dto.getOfferId());

        InvalidOfferStatusException actual = assertThrows(InvalidOfferStatusException.class, executable);

        assertThat(actual).hasMessage("Offer: " + dto.getOfferId() + " not in INITIATED status.");
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

        then.offerSaved().isTerminated();
    }

    @ParameterizedTest
    @MethodSource("offersNotInInitiatedStatus")
    void shouldInterruptRejectionIfAcceptanceNotInProgress(GivenOffer offer, OfferRepository repository) {
        OfferTestDto dto = offer.getDto();
        Executable executable = () -> offerApplicationService(repository).terminate(dto.getOfferId());

        InvalidOfferStatusException actual = assertThrows(InvalidOfferStatusException.class, executable);

        assertThat(actual).hasMessage("Offer: " + dto.getOfferId() + " not in INITIATED status.");
    }

    @ParameterizedTest
    @MethodSource("offersNotInAcceptanceInProgressStatus")
    void shouldInterruptRejectingOfferIfAcceptanceNotInProgress(GivenOffer offer, OfferRepository repository) {
        OfferTestDto dto = offer.getDto();
        Executable executable = () -> offerApplicationService(repository).reject(rejectOfferCommand());

        InvalidOfferStatusException actual = assertThrows(InvalidOfferStatusException.class, executable);

        assertThat(actual).hasMessage("Offer: " + dto.getOfferId() + " acceptance not in progress.");
    }

    @Test
    void shouldRejectOffer() {
        givenOfferWithAcceptanceInProgress();

        service.reject(rejectOfferCommand());

        then.offerSaved().isRejected();
    }

    @Test
    void shouldPublishEventWhenOfferRejected() {
        givenOfferWithAcceptanceInProgress();
        RejectOfferCommand command = rejectOfferCommand();

        service.reject(command);

        then.offerRejectedEventPublished()
                .hasOfferId(OFFER_ID)
                .hasReason("Training price changed to: 455.34 PLN")
                .isNextAfter(command.commandId());
    }

    private void givenAvailableTraining() {
        int availablePlaces = FAKER.number().numberBetween(1, 42);

        givenTraining(new TrainingDto(availablePlaces, OfferApplicationServiceTest.TRAINING_PRICE));
    }

    private void givenTraining(TrainingDto trainingDto) {
        given(trainingOfferCatalogue.detailsOf(TRAINING_ID)).willReturn(trainingDto);
    }

    private void givenOfferWithAcceptanceInProgress() {
        givenOffer().acceptanceInProgress();
    }

    private void givenInitiatedOffer() {
        givenOffer().initiated();
    }

    private GivenOffer givenOffer() {
        return given.offer(OFFER_ID).trainingId(TRAINING_ID).trainingPrice(TRAINING_PRICE);
    }

    private RejectOfferCommand rejectOfferCommand() {
        return RejectOfferCommand.nextAfter(trainingPriceChangedEvent());
    }

    private TrainingPriceChangedEvent trainingPriceChangedEvent() {
        return new TrainingPriceChangedEvent(newEventId(), OFFER_ID, TRAINING_ID, valueOf(455.34), "PLN");
    }

    private AcceptOfferCommand acceptOfferCommand() {
        OfferAcceptanceSagaEvent event = trainingPriceNotChangedEvent();
        return acceptOfferCommandBuilder(event, PARTICIPANT_ID)
                .withDiscountCodeUsed(DISCOUNT_CODE)
                .withFinalPrice(randomPrice())
                .build();
    }

    private TrainingPriceNotChangedEvent trainingPriceNotChangedEvent() {
        return new TrainingPriceNotChangedEvent(newEventId(), OFFER_ID, TRAINING_ID);
    }

    private BeginOfferAcceptanceCommand beginOfferAcceptanceCommand() {
        return BeginOfferAcceptanceCommand.nextAfter(offerAcceptanceRequestedEvent());
    }

    private OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), DISCOUNT_CODE);
    }

    private OfferAssertion thenOfferSaved(OfferRepository offerRepository) {
        return new OfferTestThen(eventRegistry, offerRepository).offerSaved();
    }

    private static Stream<Arguments> offersNotInAcceptanceInProgressStatus() {
        return Stream.of(
                offerInFinalState(GivenOffer::initiated),
                offerInFinalState(GivenOffer::accepted),
                offerInFinalState(GivenOffer::declined),
                offerInFinalState(offer -> offer.createdMinutesAgo(11).terminated()),
                offerInFinalState(GivenOffer::rejected)
        );
    }

    private static Stream<Arguments> offersNotInInitiatedStatus() {
        return Stream.of(
                offerInFinalState(GivenOffer::acceptanceInProgress),
                offerInFinalState(GivenOffer::accepted),
                offerInFinalState(GivenOffer::declined),
                offerInFinalState(offer -> offer.createdMinutesAgo(11).terminated()),
                offerInFinalState(GivenOffer::rejected)
        );
    }

    private static Arguments offerInFinalState(Consumer<GivenOffer> finalizingAction) {
        OfferRepository repository = mock(OfferRepository.class);
        GivenOfferFactory given = GivenOfferFactory.create(repository);
        GivenOffer givenOffer = given.offer(OFFER_ID).trainingId(TRAINING_ID).trainingPrice(TRAINING_PRICE);
        finalizingAction.accept(givenOffer);

        return Arguments.of(givenOffer, repository);
    }
}