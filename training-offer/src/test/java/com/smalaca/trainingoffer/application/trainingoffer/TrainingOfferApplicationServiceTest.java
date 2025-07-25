package com.smalaca.trainingoffer.application.trainingoffer;

import com.smalaca.trainingoffer.domain.commandid.CommandId;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEventAssertion;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEventAssertion;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEventAssertion.assertThatTrainingPriceChangedEvent;
import static com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEventAssertion.assertThatTrainingPriceNotChangedEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingOfferApplicationServiceTest {
    private static final UUID TRAINING_OFFER_ID = UUID.randomUUID();
    private static final UUID TRAINING_OFFER_DRAFT_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();
    private static final UUID TRAINER_ID = UUID.randomUUID();
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE_AMOUNT = BigDecimal.valueOf(1000);
    private static final int MINIMUM_PARTICIPANTS = 5;
    private static final int MAXIMUM_PARTICIPANTS = 20;
    private static final LocalDate START_DATE = LocalDate.of(2023, 10, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 10, 5);
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(17, 0);
    private static final BigDecimal NEW_PRICE_AMOUNT = BigDecimal.valueOf(1500);
    private static final String NEW_CURRENCY = "EUR";

    private final TrainingOfferRepository repository = mock(TrainingOfferRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingOfferApplicationService service = new TrainingOfferApplicationServiceFactory().trainingOfferApplicationService(repository, eventRegistry);

    private final TrainingOfferFactory factory = new TrainingOfferFactory();

    @Test
    void shouldCreateTrainingOfferWithCorrectParameters() {
        TrainingOfferPublishedEvent event = trainingOfferPublishedEvent();

        service.create(event);

        thenTrainingOfferSaved()
                .hasTrainingOfferId(TRAINING_OFFER_ID)
                .hasTrainingOfferDraftId(TRAINING_OFFER_DRAFT_ID)
                .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                .hasTrainerId(TRAINER_ID)
                .hasPrice(PRICE_AMOUNT, CURRENCY)
                .hasMinimumParticipants(MINIMUM_PARTICIPANTS)
                .hasMaximumParticipants(MAXIMUM_PARTICIPANTS)
                .hasTrainingSessionPeriod(START_DATE, END_DATE, START_TIME, END_TIME);
    }

    private TrainingOfferPublishedEvent trainingOfferPublishedEvent() {
        return TrainingOfferPublishedEvent.create(
                TRAINING_OFFER_ID, TRAINING_OFFER_DRAFT_ID, TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
    }

    private TrainingOfferAssertion thenTrainingOfferSaved() {
        ArgumentCaptor<TrainingOffer> captor = ArgumentCaptor.forClass(TrainingOffer.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingOffer(captor.getValue());
    }

    @Test
    void shouldPublishTrainingPriceChangedEventWhenPriceAmountChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(NEW_PRICE_AMOUNT, CURRENCY);

        service.confirmPrice(command);

        thenTrainingPriceChangedEventPublished()
                .isNextAfter(command.commandId())
                .hasOfferId(TRAINING_OFFER_ID)
                .hasTrainingId(TRAINING_PROGRAM_ID)
                .hasPriceAmount(NEW_PRICE_AMOUNT)
                .hasPriceCurrencyCode(CURRENCY);
    }

    @Test
    void shouldPublishTrainingPriceChangedEventWhenPriceCurrencyChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(PRICE_AMOUNT, NEW_CURRENCY);

        service.confirmPrice(command);

        thenTrainingPriceChangedEventPublished()
                .isNextAfter(command.commandId())
                .hasOfferId(TRAINING_OFFER_ID)
                .hasTrainingId(TRAINING_PROGRAM_ID)
                .hasPriceAmount(PRICE_AMOUNT)
                .hasPriceCurrencyCode(NEW_CURRENCY);
    }

    private TrainingPriceChangedEventAssertion thenTrainingPriceChangedEventPublished() {
        ArgumentCaptor<TrainingOfferEvent> eventCaptor = ArgumentCaptor.forClass(TrainingOfferEvent.class);
        then(eventRegistry).should().publish(eventCaptor.capture());
        TrainingOfferEvent actual = eventCaptor.getValue();
        assertThat(actual).isInstanceOf(TrainingPriceChangedEvent.class);

        TrainingPriceChangedEvent event = (TrainingPriceChangedEvent) actual;
        return assertThatTrainingPriceChangedEvent(event);
    }

    @Test
    void shouldPublishTrainingPriceNotChangedEventWhenPriceNotChanged() {
        existingTrainingOffer();
        ConfirmTrainingPriceCommand command = confirmTrainingPriceCommand(PRICE_AMOUNT, CURRENCY);

        service.confirmPrice(command);

        thenTrainingPriceNotChangedEventPublished()
                .isNextAfter(command.commandId())
                .hasOfferId(TRAINING_OFFER_ID)
                .hasTrainingId(TRAINING_PROGRAM_ID);
    }

    private TrainingPriceNotChangedEventAssertion thenTrainingPriceNotChangedEventPublished() {
        ArgumentCaptor<TrainingOfferEvent> eventCaptor = ArgumentCaptor.forClass(TrainingOfferEvent.class);
        then(eventRegistry).should().publish(eventCaptor.capture());
        TrainingOfferEvent actual = eventCaptor.getValue();
        assertThat(actual).isInstanceOf(TrainingPriceNotChangedEvent.class);

        return assertThatTrainingPriceNotChangedEvent((TrainingPriceNotChangedEvent) actual);
    }

    private void existingTrainingOffer() {
        given(repository.findById(TRAINING_OFFER_ID)).willReturn(trainingOffer());
    }

    private TrainingOffer trainingOffer() {
        TrainingOfferPublishedEvent event = TrainingOfferPublishedEvent.create(
                TRAINING_OFFER_ID, TRAINING_OFFER_DRAFT_ID, TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
        return factory.create(event);
    }

    private ConfirmTrainingPriceCommand confirmTrainingPriceCommand(BigDecimal amount, String currency) {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        return new ConfirmTrainingPriceCommand(commandId, TRAINING_OFFER_ID, TRAINING_PROGRAM_ID, amount, currency);
    }

}