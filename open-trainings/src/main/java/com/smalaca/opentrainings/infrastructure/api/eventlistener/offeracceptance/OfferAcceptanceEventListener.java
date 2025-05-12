package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeReturnedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
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

    @DrivenAdapter
    @KafkaListener(
            topics = "${kafka.topics.offer-acceptance.events.person-registered}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(PersonRegisteredEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(AlreadyRegisteredPersonFoundEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(NotAvailableOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(UnexpiredOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(ExpiredOfferAcceptanceRequestedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(TrainingPriceChangedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(TrainingPriceNotChangedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(DiscountCodeUsedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(DiscountCodeAlreadyUsedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(TrainingPlaceBookedEvent event) {
        engine.accept(event);
    }

    @EventListener
    @DrivenAdapter
    public void listen(NoAvailableTrainingPlacesLeftEvent event) {
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

    @EventListener
    @DrivenAdapter
    public void listen(DiscountCodeReturnedEvent event) {
        engine.accept(event);
    }
}
