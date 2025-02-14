package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.Saga;
import com.smalaca.opentrainings.application.offer.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

@Saga
public class OfferAcceptanceSaga {
    private final UUID offerId;

    private OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public static OfferAcceptanceSaga create(UUID offerId) {
        return new OfferAcceptanceSaga(offerId);
    }

    public AcceptOfferCommand accept(OfferAcceptanceRequestedEvent event) {
        return new AcceptOfferCommand(event.offerId(), event.firstName(), event.lastName(), event.email(), event.discountCode());
    }

    public boolean isCompleted() {
        return true;
    }
}
