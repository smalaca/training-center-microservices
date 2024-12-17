package com.smalaca.opentrainings.domain.order.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;

@DomainEvent
public record OrderRejectedEvent(EventId eventId, UUID orderId, String reason) implements OrderEvent {
    public static OrderRejectedEvent expired(UUID orderId) {
        return new OrderRejectedEvent(newEventId(), orderId, "Order expired");
    }
}
