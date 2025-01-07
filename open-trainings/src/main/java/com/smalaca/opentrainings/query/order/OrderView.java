package com.smalaca.opentrainings.query.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ORDERS")
@Getter
public class OrderView {
    @Id
    @Column(name = "ORDER_ID")
    private UUID orderId;

    @Column(name = "OFFER_ID")
    private UUID offerId;

    @Column(name = "TRAINING_ID")
    private UUID trainingId;

    @Column(name = "PARTICIPANT_ID")
    private UUID participantId;

    @Column(name = "ORDER_NUMBER")
    private String orderNumber;

    @Column(name = "TRAINING_PRICE_AMOUNT")
    private BigDecimal trainingPriceAmount;

    @Column(name = "TRAINING_PRICE_CURRENCY")
    private String trainingPriceCurrency;

    @Column(name = "FINAL_PRICE_AMOUNT")
    private BigDecimal finalPriceAmount;

    @Column(name = "FINAL_PRICE_CURRENCY")
    private String finalPriceCurrency;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DISCOUNT_CODE")
    private String discountCode;
}
