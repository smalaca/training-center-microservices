package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import com.smalaca.architecture.portsandadapters.SecondaryAdapter;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import org.springframework.stereotype.Component;

@Component
@SecondaryAdapter
public class JpaEventRegistry implements EventRegistry {
    @Override
    public void publish(OrderEvent event) {

    }
}
