package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.OfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferApplicationServiceFactory {
    @Bean
    public OfferApplicationService offerApplicationService(
            OfferRepository offerRepository, EventRegistry eventRegistry, TrainingOfferCatalogue trainingOfferCatalogue,
            Clock clock) {
        return new OfferApplicationService(OfferFactory.offerFactory(trainingOfferCatalogue, clock), offerRepository, eventRegistry, clock);
    }
}
