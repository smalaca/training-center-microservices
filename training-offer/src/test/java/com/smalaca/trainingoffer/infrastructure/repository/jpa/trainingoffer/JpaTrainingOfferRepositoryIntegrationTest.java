package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingoffer.domain.commandid.CommandId;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferFactory;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RepositoryTest
@Import(JpaTrainingOfferRepository.class)
class JpaTrainingOfferRepositoryIntegrationTest {
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

    @Autowired
    private TrainingOfferRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final TrainingOfferFactory factory = new TrainingOfferFactory();

    @Test
    void shouldFindNoTrainingOfferWhenDoesNotExist() {
        UUID trainingOfferId = UUID.randomUUID();
        Executable executable = () -> repository.findById(trainingOfferId);

        RuntimeException actual = assertThrows(TrainingOfferDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Training Offer with id " + trainingOfferId + " does not exist.");
    }

    @Test
    void shouldFindPublishedTrainingOfferById() {
        TrainingOffer trainingOffer = createTrainingOffer();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        TrainingOffer found = transactionTemplate.execute(transactionStatus -> repository.findById(TRAINING_OFFER_ID));
        assertThatTrainingOffer(found)
                .isPublished()
                .hasTrainingOfferId(TRAINING_OFFER_ID)
                .hasTrainingOfferDraftId(TRAINING_OFFER_DRAFT_ID)
                .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                .hasTrainerId(TRAINER_ID)
                .hasPrice(PRICE_AMOUNT, CURRENCY)
                .hasMinimumParticipants(MINIMUM_PARTICIPANTS)
                .hasMaximumParticipants(MAXIMUM_PARTICIPANTS)
                .hasNoParticipantsRegistered()
                .hasTrainingSessionPeriod(START_DATE, END_DATE, START_TIME, END_TIME);
    }

    @Test
    void shouldFindRescheduledTrainingOfferById() {
        TrainingOffer trainingOffer = createTrainingOffer();
        trainingOffer.reschedule(rescheduleTrainingOfferCommand());

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOffer));

        TrainingOffer found = transactionTemplate.execute(transactionStatus -> repository.findById(TRAINING_OFFER_ID));
        assertThatTrainingOffer(found)
                .isRescheduled()
                .hasTrainingOfferId(TRAINING_OFFER_ID)
                .hasTrainingOfferDraftId(TRAINING_OFFER_DRAFT_ID)
                .hasTrainingProgramId(TRAINING_PROGRAM_ID)
                .hasTrainerId(TRAINER_ID)
                .hasPrice(PRICE_AMOUNT, CURRENCY)
                .hasMinimumParticipants(MINIMUM_PARTICIPANTS)
                .hasMaximumParticipants(MAXIMUM_PARTICIPANTS)
                .hasNoParticipantsRegistered()
                .hasTrainingSessionPeriod(START_DATE, END_DATE, START_TIME, END_TIME);
    }

    private RescheduleTrainingOfferCommand rescheduleTrainingOfferCommand() {
        CommandId commandId = new CommandId(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        return new RescheduleTrainingOfferCommand(commandId, TRAINING_OFFER_ID, START_DATE.plusDays(1), END_DATE.plusDays(1), START_TIME, END_TIME);
    }

    private TrainingOffer createTrainingOffer() {
        TrainingOfferPublishedEvent event = TrainingOfferPublishedEvent.create(
                TRAINING_OFFER_ID, TRAINING_OFFER_DRAFT_ID, TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, CURRENCY, MINIMUM_PARTICIPANTS,
                MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
        return factory.create(event);
    }
}