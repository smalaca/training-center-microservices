package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.order;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.query.order.OrderView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TrainingPurchasedPivotalEvent(
        EventId eventId, UUID orderId, UUID offerId, UUID trainingId, UUID participantId, String orderNumber,
        BigDecimal trainingPriceAmount, String trainingPriceCurrency, BigDecimal finalPriceAmount,
        String finalPriceCurrency, LocalDateTime orderCreationDateTime, String discountCode) {
    public TrainingPurchasedPivotalEvent(TrainingPurchasedEvent event, OrderView order) {
        this(
                event.eventId().nextEventId(),
                event.orderId(),
                event.offerId(),
                event.trainingId(),
                event.participantId(),
                order.getOrderNumber(),
                order.getTrainingPriceAmount(),
                order.getTrainingPriceCurrency(),
                order.getFinalPriceAmount(),
                order.getFinalPriceCurrency(),
                order.getCreationDateTime(),
                order.getDiscountCode()
        );
    }
}
