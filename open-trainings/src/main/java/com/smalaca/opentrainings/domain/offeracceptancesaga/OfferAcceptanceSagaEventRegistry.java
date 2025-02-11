package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

@DrivenPort
public interface OfferAcceptanceSagaEventRegistry {
    void publish(OfferAcceptanceSagaEvent event);
}
