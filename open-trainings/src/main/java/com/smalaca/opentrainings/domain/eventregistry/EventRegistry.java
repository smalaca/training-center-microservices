package com.smalaca.opentrainings.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.SecondaryPort;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;

@SecondaryPort
public interface EventRegistry {
    void publish(OrderEvent event);
}
