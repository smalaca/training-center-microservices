package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAlreadyPublishedException;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEventAssertion;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion.assertThatTrainingOfferDraft;
import static com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEventAssertion.assertThatTrainingOfferPublishedEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingOfferDraftApplicationServiceTest {
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

    private final GivenTrainingOfferDraftFactory given = GivenTrainingOfferDraftFactory.create();

    private final TrainingOfferDraftRepository repository = mock(TrainingOfferDraftRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingOfferDraftApplicationService service = new TrainingOfferDraftApplicationServiceFactory().trainingOfferDraftApplicationService(repository, eventRegistry);

    @Test
    void shouldPublishTrainingOfferPublishedEventWhenTrainingOfferDraftIsPublished() {
        TrainingOfferDraftTestDto expected = givenExisting(given.trainingOfferDraft().initiated());

        service.publish(TRAINING_OFFER_DRAFT_ID);

        thenTrainingOfferPublishedEventPublished()
                .hasTrainingOfferDraftId(TRAINING_OFFER_DRAFT_ID)
                .hasTrainingProgramId(expected.getTrainingProgramId())
                .hasTrainerId(expected.getTrainerId())
                .hasPriceAmount(expected.getPriceAmount())
                .hasPriceCurrency(expected.getPriceCurrency())
                .hasMinimumParticipants(expected.getMinimumParticipants())
                .hasMaximumParticipants(expected.getMaximumParticipants())
                .hasStartDate(expected.getStartDate())
                .hasEndDate(expected.getEndDate())
                .hasStartTime(expected.getStartTime())
                .hasEndTime(expected.getEndTime());
    }

    @Test
    void shouldPublishTrainingOfferPublishedWithTrainingOfferId() {
        givenExisting(given.trainingOfferDraft().initiated());

        UUID actual = service.publish(TRAINING_OFFER_DRAFT_ID);

        thenTrainingOfferPublishedEventPublished().hasTrainingOfferId(actual);
    }

    @Test
    void shouldThrowExceptionWhenTrainingOfferDraftAlreadyPublished() {
        givenExisting(given.trainingOfferDraft().published());

        TrainingOfferDraftAlreadyPublishedException actual = assertThrows(TrainingOfferDraftAlreadyPublishedException.class, () -> service.publish(TRAINING_OFFER_DRAFT_ID));

        assertThat(actual).hasMessage("Training offer draft: " + TRAINING_OFFER_DRAFT_ID + " already published.");
    }

    @Test
    void shouldMarkTrainingOfferDraftAsPublishedWhenEventIsReceived() {
        givenExisting(given.trainingOfferDraft().initiated());

        service.publish(TRAINING_OFFER_DRAFT_ID);

        thenTrainingOfferDraftSaved().isPublished();
    }

    @Test
    void shouldReturnTrainingOfferDraftIdWhenCreated() {
        CreateTrainingOfferDraftCommand command = createTrainingOfferDraftCommand();
        given(repository.save(any())).willReturn(TRAINING_OFFER_DRAFT_ID);

        UUID actual = service.create(command);

        assertThat(actual).isEqualTo(TRAINING_OFFER_DRAFT_ID);
    }
    
    @Test
    void shouldCreateTrainingOfferDraftWithCorrectParameters() {
        CreateTrainingOfferDraftCommand command = createTrainingOfferDraftCommand();

        service.create(command);

        thenTrainingOfferDraftSaved()
                .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                .hasTrainerId(TRAINER_ID)
                .hasPrice(PRICE_AMOUNT, CURRENCY)
                .hasMinimumParticipants(MINIMUM_PARTICIPANTS)
                .hasMaximumParticipants(MAXIMUM_PARTICIPANTS)
                .hasTrainingSessionPeriod(START_DATE, END_DATE, START_TIME, END_TIME)
                .isNotPublished();
    }

    private TrainingOfferDraftTestDto givenExisting(GivenTrainingOfferDraft given) {
        TrainingOfferDraft trainingOfferDraft = given.getTrainingOfferDraft();
        assignTrainingOfferDraftIdTo(trainingOfferDraft);
        given(repository.findById(TRAINING_OFFER_DRAFT_ID)).willReturn(trainingOfferDraft);

        return given.getDto();
    }

    private void assignTrainingOfferDraftIdTo(TrainingOfferDraft trainingOfferDraft) {
        try {
            Field offerIdField = trainingOfferDraft.getClass().getDeclaredField("trainingOfferDraftId");
            offerIdField.setAccessible(true);
            offerIdField.set(trainingOfferDraft, TRAINING_OFFER_DRAFT_ID);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private TrainingOfferDraftAssertion thenTrainingOfferDraftSaved() {
        ArgumentCaptor<TrainingOfferDraft> captor = ArgumentCaptor.forClass(TrainingOfferDraft.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingOfferDraft(captor.getValue());
    }

    private TrainingOfferPublishedEventAssertion thenTrainingOfferPublishedEventPublished() {
        ArgumentCaptor<TrainingOfferPublishedEvent> captor = ArgumentCaptor.forClass(TrainingOfferPublishedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return assertThatTrainingOfferPublishedEvent(captor.getValue());
    }
    
    private CreateTrainingOfferDraftCommand createTrainingOfferDraftCommand() {
        return new CreateTrainingOfferDraftCommand(
                TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
    }
}
