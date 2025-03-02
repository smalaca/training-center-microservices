package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
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
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterRegisterPersonCommand = new HashMap<>();
    private final Map<UUID, OfferAcceptanceSagaEvent> eventsAfterConfirmTrainingPriceCommand = new HashMap<>();

    OfferAcceptanceSagaEventTestListener(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    void listen(RegisterPersonCommand command) {
        publisher.publishEvent(eventsAfterRegisterPersonCommand.get(command.offerId()));
    }

    void willReturnAfterRegisterPersonCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterRegisterPersonCommand.put(offerId, event);
    }

    @EventListener
    void listen(ConfirmTrainingPriceCommand command) {
        publisher.publishEvent(eventsAfterConfirmTrainingPriceCommand.get(command.offerId()));
    }

    void willReturnAfterConfirmTrainingPriceCommand(UUID offerId, OfferAcceptanceSagaEvent event) {
        eventsAfterConfirmTrainingPriceCommand.put(offerId, event);
    }
}
