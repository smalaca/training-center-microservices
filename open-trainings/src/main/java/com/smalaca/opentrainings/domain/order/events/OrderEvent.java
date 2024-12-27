package com.smalaca.opentrainings.domain.order.events;

import com.smalaca.opentrainings.domain.eventid.EventId;

public interface OrderEvent {
    EventId eventId();
}
