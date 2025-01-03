package com.smalaca.opentrainings.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;

@DrivenPort
public interface EventRegistry {
    void publish(OrderEvent event);

    void publish(OfferEvent event);
}
