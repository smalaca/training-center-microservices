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
public class OrderDto {
    @Id
    @Column(name = "ORDER_ID")
    private UUID orderId;

    @Column(name = "OFFER_ID")
    private UUID offerId;

    @Column(name = "TRAINING_ID")
    private UUID trainingId;

    @Column(name = "PARTICIPANT_ID")
    private UUID participantId;

    @Column(name = "PRICE_AMOUNT")
    private BigDecimal priceAmount;

    @Column(name = "PRICE_CURRENCY")
    private String priceCurrency;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Column(name = "STATUS")
    private String status;
}
