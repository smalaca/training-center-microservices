package com.smalaca.opentrainings.domain.order.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

@DomainEvent
public record TrainingPurchasedEvent(EventId eventId, UUID orderId, UUID trainingId, UUID participantId) implements OrderEvent {
    public static TrainingPurchasedEvent create(UUID orderId, UUID trainingId, UUID participantId) {
        return new TrainingPurchasedEvent(EventId.newEventId(), orderId, trainingId, participantId);
    }
}
