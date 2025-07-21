package com.smalaca.trainingoffer.application.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingOfferApplicationServiceTest {
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

    private final TrainingOfferRepository repository = mock(TrainingOfferRepository.class);
    private final TrainingOfferApplicationService service = new TrainingOfferApplicationServiceFactory().trainingOfferApplicationService(repository);
    
    @Test
    void shouldCreateTrainingOfferWithCorrectParameters() {
        TrainingOfferPublishedEvent event = trainingOfferPublishedEvent();

        service.create(event);

        thenTrainingOfferSaved()
                .hasTrainingOfferIdNull()
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
                TRAINING_OFFER_DRAFT_ID, TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
    }

    private TrainingOfferAssertion thenTrainingOfferSaved() {
        ArgumentCaptor<TrainingOffer> captor = ArgumentCaptor.forClass(TrainingOffer.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingOffer(captor.getValue());
    }
}