package com.smalaca.opentrainings.infrastructure.api.eventlistener.order;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.application.order.OrderApplicationService;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final OrderApplicationService orderApplicationService;

    OrderEventListener(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferAcceptedEvent event) {
        orderApplicationService.initiate(event);
    }
}