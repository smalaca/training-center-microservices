package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
class OfferAcceptanceSagaEventTestListener {
    private final ApplicationEventPublisher publisher;
    private final Map<UUID, OfferAcceptanceSagaEvent> events = new HashMap<>();

    OfferAcceptanceSagaEventTestListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    void listen(RegisterPersonCommand command) {
        publisher.publishEvent(events.get(command.offerId()));
    }

    void willReturn(UUID offerId, OfferAcceptanceSagaEvent event) {
        events.put(offerId, event);
    }
}
