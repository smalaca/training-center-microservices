package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OfferAcceptanceEventListener {
    private final OfferAcceptanceSagaEngine engine;

    OfferAcceptanceEventListener(OfferAcceptanceSagaEngine engine) {
        this.engine = engine;
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferAcceptedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(OfferRejectedEvent event) {
        engine.accept(event);
    }
}
