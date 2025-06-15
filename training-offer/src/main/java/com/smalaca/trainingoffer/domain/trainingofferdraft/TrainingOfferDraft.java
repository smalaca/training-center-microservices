package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AggregateRoot
public class TrainingOfferDraft {
    private UUID trainingOfferDraftId;
    private final UUID trainingProgramId;
    private final UUID trainerId;
    private final Price price;
    private final int minimumParticipants;
    private final int maximumParticipants;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean published;

    private TrainingOfferDraft(Builder builder) {
        this.trainingProgramId = builder.trainingProgramId;
        this.trainerId = builder.trainerId;
        this.price = builder.price;
        this.minimumParticipants = builder.minimumParticipants;
        this.maximumParticipants = builder.maximumParticipants;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.published = false;
    }

    public TrainingOfferPublishedEvent publish() {
        this.published = true;

        return TrainingOfferPublishedEvent.create(
            trainingOfferDraftId, 
            trainingProgramId, 
            trainerId, 
            price, 
            minimumParticipants, 
            maximumParticipants, 
            startDate, 
            endDate, 
            startTime, 
            endTime
        );
    }

    @Factory
    public static class Builder {
        private UUID trainingProgramId;
        private UUID trainerId;
        private Price price;
        private int minimumParticipants;
        private int maximumParticipants;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;

        public Builder withTrainingProgramId(UUID trainingProgramId) {
            this.trainingProgramId = trainingProgramId;
            return this;
        }

        public Builder withTrainerId(UUID trainerId) {
            this.trainerId = trainerId;
            return this;
        }

        public Builder withPrice(Price price) {
            this.price = price;
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

        public Builder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder withStartTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public TrainingOfferDraft build() {
            return new TrainingOfferDraft(this);
        }
    }
}
