package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TrainingOfferDraftTestDto {
    private final UUID trainingOfferDraftId;
    private final UUID trainingProgramId;
    private final UUID trainerId;
    private final Price price;
    private final int minimumParticipants;
    private final int maximumParticipants;
    private final TrainingSessionPeriod trainingSessionPeriod;

    private TrainingOfferDraftTestDto(Builder builder) {
        this.trainingOfferDraftId = builder.trainingOfferDraftId;
        this.trainingProgramId = builder.trainingProgramId;
        this.trainerId = builder.trainerId;
        this.price = builder.price;
        this.minimumParticipants = builder.minimumParticipants;
        this.maximumParticipants = builder.maximumParticipants;
        this.trainingSessionPeriod = builder.trainingSessionPeriod;
    }

    public UUID getTrainingOfferDraftId() {
        return trainingOfferDraftId;
    }

    public UUID getTrainingProgramId() {
        return trainingProgramId;
    }

    public UUID getTrainerId() {
        return trainerId;
    }

    public Price getPrice() {
        return price;
    }

    public int getMinimumParticipants() {
        return minimumParticipants;
    }

    public int getMaximumParticipants() {
        return maximumParticipants;
    }

    public TrainingSessionPeriod getTrainingSessionPeriod() {
        return trainingSessionPeriod;
    }

    static class Builder {
        private UUID trainingOfferDraftId;
        private UUID trainingProgramId;
        private UUID trainerId;
        private Price price;
        private int minimumParticipants;
        private int maximumParticipants;
        private TrainingSessionPeriod trainingSessionPeriod;

        Builder withTrainingOfferDraftId(UUID trainingOfferDraftId) {
            this.trainingOfferDraftId = trainingOfferDraftId;
            return this;
        }

        Builder withTrainingProgramId(UUID trainingProgramId) {
            this.trainingProgramId = trainingProgramId;
            return this;
        }

        Builder withTrainerId(UUID trainerId) {
            this.trainerId = trainerId;
            return this;
        }

        Builder withPrice(BigDecimal amount, String currency) {
            this.price = Price.of(amount, currency);
            return this;
        }

        Builder withMinimumParticipants(int minimumParticipants) {
            this.minimumParticipants = minimumParticipants;
            return this;
        }

        Builder withMaximumParticipants(int maximumParticipants) {
            this.maximumParticipants = maximumParticipants;
            return this;
        }

        Builder withTrainingSessionPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
            this.trainingSessionPeriod = new TrainingSessionPeriod(startDate, endDate, startTime, endTime);
            return this;
        }

        TrainingOfferDraftTestDto build() {
            return new TrainingOfferDraftTestDto(this);
        }
    }
}