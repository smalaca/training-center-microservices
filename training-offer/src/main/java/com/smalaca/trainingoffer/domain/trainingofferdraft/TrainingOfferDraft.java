package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;
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
    private final TrainingSessionPeriod trainingSessionPeriod;
    private boolean published;

    private TrainingOfferDraft(Builder builder) {
        this.trainingProgramId = builder.trainingProgramId;
        this.trainerId = builder.trainerId;
        this.price = builder.price;
        this.minimumParticipants = builder.minimumParticipants;
        this.maximumParticipants = builder.maximumParticipants;
        this.trainingSessionPeriod = builder.trainingSessionPeriod;
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
            trainingSessionPeriod.startDate(), 
            trainingSessionPeriod.endDate(), 
            trainingSessionPeriod.startTime(), 
            trainingSessionPeriod.endTime()
        );
    }

    @Factory
    public static class Builder {
        private UUID trainingProgramId;
        private UUID trainerId;
        private Price price;
        private int minimumParticipants;
        private int maximumParticipants;
        private TrainingSessionPeriod trainingSessionPeriod;

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

        public Builder withTrainingSessionPeriod(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
            this.trainingSessionPeriod = new TrainingSessionPeriod(startDate, endDate, startTime, endTime);
            return this;
        }

        public TrainingOfferDraft build() {
            return new TrainingOfferDraft(this);
        }
    }
}
