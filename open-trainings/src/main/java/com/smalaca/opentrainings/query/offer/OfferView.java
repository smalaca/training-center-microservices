package com.smalaca.opentrainings.query.offer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "OFFERS")
@Getter
public class OfferView {
    @Id
    @Column(name = "OFFER_ID")
    private UUID offerId;

    @Column(name = "TRAINING_ID")
    private UUID trainingId;

    @Column(name = "OFFER_NUMBER")
    private String offerNumber;

    @Column(name = "TRAINING_PRICE_AMOUNT")
    private BigDecimal trainingPriceAmount;

    @Column(name = "TRAINING_PRICE_CURRENCY")
    private String trainingPriceCurrency;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Column(name = "STATUS")
    private String status;
}
