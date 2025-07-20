package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.trainingoffer.domain.trainingofferdraft.commands.CreateTrainingOfferDraftCommand;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

public class GivenTrainingOfferDraft {
    private static final Faker FAKER = new Faker();

    private final UUID trainingProgramId = randomUUID();
    private final UUID trainerId = randomUUID();
    private final BigDecimal priceAmount = BigDecimal.valueOf(FAKER.number().numberBetween(100L, 10000L));
    private final String priceCurrency = FAKER.currency().code();
    private final int minimumParticipants = 5;
    private final int maximumParticipants = 20;
    private final LocalDate startDate = LocalDate.now().plusDays(FAKER.number().numberBetween(1L, 100L));
    private final LocalDate endDate = LocalDate.now().plusDays(FAKER.number().numberBetween(1L, 5L));
    private final LocalTime startTime = LocalTime.of(9, 0);
    private final LocalTime endTime = LocalTime.of(17, 0);
    private final TrainingOfferDraftFactory factory;

    private TrainingOfferDraft trainingOfferDraft;

    GivenTrainingOfferDraft(TrainingOfferDraftFactory factory) {
        this.factory = factory;
    }

    public GivenTrainingOfferDraft initiated() {
        CreateTrainingOfferDraftCommand command = new CreateTrainingOfferDraftCommand(
                trainingProgramId, trainerId, priceAmount, priceCurrency, minimumParticipants, maximumParticipants,
                startDate, endDate, startTime, endTime);
        trainingOfferDraft = factory.create(command);

        return this;
    }

    public GivenTrainingOfferDraft published() {
        initiated();
        trainingOfferDraft.publish();
        return this;
    }

    public TrainingOfferDraft getTrainingOfferDraft() {
        return trainingOfferDraft;
    }

    public TrainingOfferDraftTestDto getDto() {
        return new TrainingOfferDraftTestDto.Builder()
                .withTrainingOfferDraftId(trainingOfferDraft.trainingOfferDraftId())
                .withTrainingProgramId(trainingProgramId)
                .withTrainerId(trainerId)
                .withPrice(priceAmount, priceCurrency)
                .withMinimumParticipants(minimumParticipants)
                .withMaximumParticipants(maximumParticipants)
                .withTrainingSessionPeriod(startDate, endDate, startTime, endTime)
                .build();
    }
}