package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.OfferFactory;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;

class OfferApplicationServiceFactory {
    OfferApplicationService offerApplicationService(
            OfferRepository offerRepository, EventRegistry eventRegistry, PersonalDataManagement personalDataManagement,
            TrainingOfferCatalogue trainingOfferCatalogue, DiscountService discountService, Clock clock) {
        return new OfferApplicationService(
                OfferFactory.offerFactory(trainingOfferCatalogue, clock), offerRepository, eventRegistry, personalDataManagement,
                trainingOfferCatalogue, discountService, clock);
    }
}
