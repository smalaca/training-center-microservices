package com.smalaca.opentrainings.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;

@DrivenPort
public interface EventRegistry {
    void publish(OrderEvent event);
}
