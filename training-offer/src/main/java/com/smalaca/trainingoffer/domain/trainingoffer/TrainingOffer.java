package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "TRAINING_OFFERS")
public class TrainingOffer {
    @Id
    @GeneratedValue
    @Column(name = "TRAINING_OFFER_ID")
    private UUID trainingOfferId;

    @Column(name = "TRAINING_OFFER_DRAFT_ID")
    private UUID trainingOfferDraftId;

    @Column(name = "TRAINING_PROGRAM_ID")
    private UUID trainingProgramId;

    @Column(name = "TRAINER_ID")
    private UUID trainerId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "PRICE_AMOUNT"))
    @AttributeOverride(name = "currency", column = @Column(name = "PRICE_CURRENCY"))
    private Price price;

    @Column(name = "MINIMUM_PARTICIPANTS")
    private int minimumParticipants;

    @Column(name = "MAXIMUM_PARTICIPANTS")
    private int maximumParticipants;

    @Embedded
    @AttributeOverride(name = "startDate", column = @Column(name = "START_DATE"))
    @AttributeOverride(name = "endDate", column = @Column(name = "END_DATE"))
    @AttributeOverride(name = "startTime", column = @Column(name = "START_TIME"))
    @AttributeOverride(name = "endTime", column = @Column(name = "END_TIME"))
    private TrainingSessionPeriod trainingSessionPeriod;

    private TrainingOffer() {}

    private TrainingOffer(Builder builder) {
        this.trainingOfferDraftId = builder.trainingOfferDraftId;
        this.trainingProgramId = builder.trainingProgramId;
        this.trainerId = builder.trainerId;
        this.price = builder.price;
        this.minimumParticipants = builder.minimumParticipants;
        this.maximumParticipants = builder.maximumParticipants;
        this.trainingSessionPeriod = builder.trainingSessionPeriod;
    }

    @Factory
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

        TrainingOffer build() {
            return new TrainingOffer(this);
        }
    }
}