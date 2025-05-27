package com.smalaca.opentrainings.infrastructure.api.eventlistener.offeracceptance;

import com.smalaca.opentrainings.application.offeracceptancesaga.OfferAcceptanceSagaEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OfferAcceptanceEventListenerFactory {
    @Bean
    public OfferAcceptanceEventListener offerAcceptanceEventListener(OfferAcceptanceSagaEngine offerAcceptanceSagaEngine) {
        return new OfferAcceptanceEventListener(new ThreadSafeOfferAcceptanceSagaEngine(offerAcceptanceSagaEngine));
    }
}
