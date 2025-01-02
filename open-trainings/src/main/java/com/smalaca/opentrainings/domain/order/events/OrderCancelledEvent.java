package com.smalaca.opentrainings.domain.order.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

@DomainEvent
public record OrderCancelledEvent(EventId eventId, UUID orderId, UUID offerId, UUID trainingId, UUID participantId) implements OrderEvent {
    public static OrderCancelledEvent create(UUID orderId, UUID offerId, UUID trainingId, UUID participantId) {
        return new OrderCancelledEvent(EventId.newEventId(), orderId, offerId, trainingId, participantId);
    }
}
