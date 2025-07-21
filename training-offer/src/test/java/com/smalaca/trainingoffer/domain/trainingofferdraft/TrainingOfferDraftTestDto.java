package com.smalaca.trainingoffer.domain.trainingofferdraft;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class TrainingOfferDraftTestDto {
    private final UUID trainingOfferDraftId;
    private final UUID trainingProgramId;
    private final UUID trainerId;
    private final BigDecimal priceAmount;
    private final String priceCurrency;
    private final int minimumParticipants;
    private final int maximumParticipants;
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;

    private TrainingOfferDraftTestDto(Builder builder) {
        this.trainingOfferDraftId = builder.trainingOfferDraftId;
        this.trainingProgramId = builder.trainingProgramId;
        this.trainerId = builder.trainerId;
        this.priceAmount = builder.priceAmount;
        this.priceCurrency = builder.priceCurrency;
        this.minimumParticipants = builder.minimumParticipants;
        this.maximumParticipants = builder.maximumParticipants;
        this.startDate = builder.startDate;
        this.startTime = builder.startTime;
        this.endDate = builder.endDate;
        this.endTime = builder.endTime;
    }

    public static class Builder {
        private UUID trainingOfferDraftId;
        private UUID trainingProgramId;
        private UUID trainerId;
        private BigDecimal priceAmount;
        private String priceCurrency;
        private int minimumParticipants;
        private int maximumParticipants;
        private LocalDate startDate;
        private LocalTime startTime;
        private LocalDate endDate;
        private LocalTime endTime;

        public Builder withTrainingOfferDraftId(UUID trainingOfferDraftId) {
            this.trainingOfferDraftId = trainingOfferDraftId;
            return this;
        }

        public Builder withTrainingProgramId(UUID trainingProgramId) {
            this.trainingProgramId = trainingProgramId;
            return this;
        }

        public Builder withTrainerId(UUID trainerId) {
            this.trainerId = trainerId;
            return this;
        }

        public Builder withPrice(BigDecimal priceAmount, String priceCurrency) {
            this.priceAmount = priceAmount;
            this.priceCurrency = priceCurrency;
            return this;
        }

        public Builder withMinimumParticipants(int minimumParticipants) {
            this.minimumParticipants = minimumParticipants;
            return this;
        }

        public Builder withMaximumParticipants(int maximumParticipants) {
            this.maximumParticipants = maximumParticipants;
            return this;
        }

        public Builder withTrainingSessionPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.endTime = endTime;
            return this;
        }

        public TrainingOfferDraftTestDto build() {
            return new TrainingOfferDraftTestDto(this);
        }
    }
}