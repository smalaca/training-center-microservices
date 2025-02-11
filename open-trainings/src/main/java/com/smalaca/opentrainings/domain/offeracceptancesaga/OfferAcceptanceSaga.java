package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.Saga;
import com.smalaca.opentrainings.application.offer.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;

import java.util.UUID;

@Saga
public class OfferAcceptanceSaga {
    private static final Boolean COMPLETED = true;
    private static final Boolean NOT_COMPLETED = false;

    private final UUID offerId;
    private Boolean isCompleted = NOT_COMPLETED;

    public OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public AcceptOfferCommand accept(OfferAcceptanceRequestedEvent event) {
        isCompleted = COMPLETED;
        return new AcceptOfferCommand(event.offerId(), event.firstName(), event.lastName(), event.email(), event.discountCode());
    }
}
