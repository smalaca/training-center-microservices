package com.smalaca.trainingoffer.domain.trainingoffer;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.RescheduleTrainingOfferCommand;
import com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingsessionperiod.TrainingSessionPeriod;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferStatus.PUBLISHED;

@AggregateRoot
@Entity
@Table(name = "TRAINING_OFFERS")
public class TrainingOffer {
    @Id
    @Column(name = "TRAINING_OFFER_ID")
    private UUID trainingOfferId;

    @Column(name = "TRAINING_OFFER_DRAFT_ID")
    private UUID trainingOfferDraftId;

    @Column(name = "TRAINER_ID")
    private UUID trainerId;

    @Column(name = "TRAINING_PROGRAM_ID")
    private UUID trainingProgramId;

    @Embedded
    @AttributeOverride(name = "startDate", column = @Column(name = "START_DATE"))
    @AttributeOverride(name = "endDate", column = @Column(name = "END_DATE"))
    @AttributeOverride(name = "startTime", column = @Column(name = "START_TIME"))
    @AttributeOverride(name = "endTime", column = @Column(name = "END_TIME"))
    private TrainingSessionPeriod trainingSessionPeriod;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "PRICE_AMOUNT"))
    @AttributeOverride(name = "currency", column = @Column(name = "PRICE_CURRENCY"))
    private Price price;

    @Embedded
    private Participants participants;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TrainingOfferStatus status;

    private TrainingOffer() {}

    private TrainingOffer(Builder builder, TrainingOfferStatus status, Participants participants) {
        this.trainingOfferId = builder.trainingOfferId;
        this.trainingOfferDraftId = builder.trainingOfferDraftId;
        this.trainerId = builder.trainerId;
        this.trainingProgramId = builder.trainingProgramId;
        this.trainingSessionPeriod = builder.trainingSessionPeriod;
        this.price = builder.price;
        this.status = status;
        this.participants = participants;
    }
    
    public TrainingOfferEvent confirmPrice(ConfirmTrainingPriceCommand command) {
        Price toConfirm = Price.of(command.priceAmount(), command.priceCurrencyCode());

        if (price.equals(toConfirm)) {
            return TrainingPriceNotChangedEvent.nextAfter(command);
        } else {
            return TrainingPriceChangedEvent.nextAfter(command, price);
        }
    }
    
    public TrainingOfferEvent book(BookTrainingPlaceCommand command) {
        if (participants.hasAvailablePlaces()) {
            participants.addParticipant(command.participantId());
            return TrainingPlaceBookedEvent.nextAfter(command);
        } else {
            return NoAvailableTrainingPlacesLeftEvent.nextAfter(command);
        }
    }
    
    public TrainingOfferRescheduledEvent reschedule(RescheduleTrainingOfferCommand command) {
        this.status = TrainingOfferStatus.RESCHEDULED;
        
        return TrainingOfferRescheduledEvent.nextAfter(
            command, newTrainingOfferId(),
            trainingOfferDraftId,
            trainingProgramId, 
            trainerId, 
            price.amount(), 
            price.currencyCode(), 
            participants.minimumParticipants(), 
            participants.maximumParticipants()
        );
    }

    private UUID newTrainingOfferId() {
        return UUID.randomUUID();
    }

    @Factory
    static class Builder {
        private UUID trainingOfferId;
        private UUID trainingOfferDraftId;
        private UUID trainerId;
        private UUID trainingProgramId;
        private TrainingSessionPeriod trainingSessionPeriod;
        private Price price;
        private int minimumParticipants;
        private int maximumParticipants;

        Builder withTrainingOfferId(UUID trainingOfferId) {
            this.trainingOfferId = trainingOfferId;
            return this;
        }

        Builder withTrainingOfferDraftId(UUID trainingOfferDraftId) {
            this.trainingOfferDraftId = trainingOfferDraftId;
            return this;
        }

        Builder withTrainerId(UUID trainerId) {
            this.trainerId = trainerId;
            return this;
        }

        Builder withTrainingProgramId(UUID trainingProgramId) {
            this.trainingProgramId = trainingProgramId;
            return this;
        }

        Builder withTrainingSessionPeriod(TrainingSessionPeriod trainingSessionPeriod) {
            this.trainingSessionPeriod = trainingSessionPeriod;
            return this;
        }

        Builder withPrice(Price price) {
            this.price = price;
            return this;
        }

        Builder withMaximumParticipants(int maximumParticipants) {
            this.maximumParticipants = maximumParticipants;
            return this;
        }

        Builder withMinimumParticipants(int minimumParticipants) {
            this.minimumParticipants = minimumParticipants;
            return this;
        }

        TrainingOffer build() {
            return new TrainingOffer(this, PUBLISHED, Participants.from(minimumParticipants, maximumParticipants));
        }
    }
}