package com.smalaca.trainingoffer.domain.trainingofferdraft;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@AggregateRoot
@Entity
@Table(name = "TRAINING_OFFER_DRAFTS")
public class TrainingOfferDraft {
    @Id
    @GeneratedValue
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

    @Column(name = "PUBLISHED")
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

    private TrainingOfferDraft() {
    }

    public TrainingOfferPublishedEvent publish() {
        if (published) {
            throw new TrainingOfferDraftAlreadyPublishedException(trainingOfferDraftId);
        }

        published = true;

        return TrainingOfferPublishedEvent.create(
                trainingOfferId(),
                trainingOfferDraftId,
                trainingProgramId,
                trainerId,
                price.amount(),
                price.currencyCode(),
                minimumParticipants,
                maximumParticipants,
                trainingSessionPeriod.startDate(),
                trainingSessionPeriod.endDate(),
                trainingSessionPeriod.startTime(),
                trainingSessionPeriod.endTime()
        );
    }

    private UUID trainingOfferId() {
        return UUID.randomUUID();
    }

    public UUID trainingOfferDraftId() {
        return trainingOfferDraftId;
    }

    @Factory
    static class Builder {
        private UUID trainingProgramId;
        private UUID trainerId;
        private Price price;
        private int minimumParticipants;
        private int maximumParticipants;
        private TrainingSessionPeriod trainingSessionPeriod;

        Builder withTrainingProgramId(UUID trainingProgramId) {
            this.trainingProgramId = trainingProgramId;
            return this;
        }

        Builder withTrainerId(UUID trainerId) {
            this.trainerId = trainerId;
            return this;
        }

        Builder withPrice(Price price) {
            this.price = price;
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

        Builder withTrainingSessionPeriod(TrainingSessionPeriod trainingSessionPeriod) {
            this.trainingSessionPeriod = trainingSessionPeriod;
            return this;
        }

        TrainingOfferDraft build() {
            return new TrainingOfferDraft(this);
        }
    }
}
