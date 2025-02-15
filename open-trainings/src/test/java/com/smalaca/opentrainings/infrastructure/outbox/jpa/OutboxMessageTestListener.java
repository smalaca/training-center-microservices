package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class OutboxMessageTestListener {
    List<OfferRejectedEvent> offerRejectedEvents = new ArrayList<>();
    List<OrderRejectedEvent> orderRejectedEvents = new ArrayList<>();

    @EventListener
    void listen(OfferRejectedEvent event) {
        offerRejectedEvents.add(event);
    }

    @EventListener
    void listen(OrderRejectedEvent event) {
        orderRejectedEvents.add(event);
    }
}
