package com.smalaca.trainingscatalogue.trainingoffer;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "TRAINING_OFFERS")
@Getter
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

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "END_TIME")
    private LocalTime endTime;

    @Column(name = "PRICE_AMOUNT")
    private BigDecimal priceAmount;

    @Column(name = "PRICE_CURRENCY")
    private String priceCurrency;

    @Column(name = "MINIMUM_PARTICIPANTS")
    private int minimumParticipants;

    @Column(name = "MAXIMUM_PARTICIPANTS")
    private int maximumParticipants;

    private TrainingOffer() {}

    public TrainingOffer(TrainingOfferPublishedEvent event) {
        this.trainingOfferId = event.trainingOfferId();
        this.trainingOfferDraftId = event.trainingOfferDraftId();
        this.trainerId = event.trainerId();
        this.trainingProgramId = event.trainingProgramId();
        this.startDate = event.startDate();
        this.endDate = event.endDate();
        this.startTime = event.startTime();
        this.endTime = event.endTime();
        this.priceAmount = event.priceAmount();
        this.priceCurrency = event.priceCurrencyCode();
        this.minimumParticipants = event.minimumParticipants();
        this.maximumParticipants = event.maximumParticipants();
    }
}