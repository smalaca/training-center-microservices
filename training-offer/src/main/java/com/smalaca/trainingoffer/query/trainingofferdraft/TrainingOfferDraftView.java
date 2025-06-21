package com.smalaca.trainingoffer.query.trainingofferdraft;

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
@Table(name = "TRAINING_OFFER_DRAFTS")
@Getter
public class TrainingOfferDraftView {
    @Id
    @Column(name = "TRAINING_OFFER_DRAFT_ID")
    private UUID trainingOfferDraftId;

    @Column(name = "TRAINING_PROGRAM_ID")
    private UUID trainingProgramId;

    @Column(name = "TRAINER_ID")
    private UUID trainerId;

    @Column(name = "PRICE_AMOUNT")
    private BigDecimal priceAmount;

    @Column(name = "PRICE_CURRENCY")
    private String priceCurrency;

    @Column(name = "MINIMUM_PARTICIPANTS")
    private int minimumParticipants;

    @Column(name = "MAXIMUM_PARTICIPANTS")
    private int maximumParticipants;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "END_TIME")
    private LocalTime endTime;

    @Column(name = "PUBLISHED")
    private boolean published;
}