package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class ThreadSafeOfferAcceptanceSagaEngine {
    private final OfferAcceptanceSagaEngine engine;
    private final Map<UUID, Object> locks = new ConcurrentHashMap<>();

    ThreadSafeOfferAcceptanceSagaEngine(OfferAcceptanceSagaEngine engine) {
        this.engine = engine;
    }

    private void synchronizedExecute(UUID offerId, SagaOperation operation) {
        Object lock = locks.computeIfAbsent(offerId, k -> new Object());
        synchronized (lock) {
            try {
                operation.execute();
            } finally {
                locks.remove(offerId);
            }
        }
    }

    private interface SagaOperation {
        void execute();
    }

    void accept(OfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(PersonRegisteredEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(AlreadyRegisteredPersonFoundEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(UnexpiredOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(ExpiredOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(TrainingPriceNotChangedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(TrainingPriceChangedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(DiscountCodeUsedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(DiscountCodeAlreadyUsedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(TrainingPlaceBookedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(NoAvailableTrainingPlacesLeftEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(NotAvailableOfferAcceptanceRequestedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(OfferAcceptedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(OfferRejectedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }

    void accept(DiscountCodeReturnedEvent event) {
        synchronizedExecute(event.offerId(), () -> engine.accept(event));
    }
}