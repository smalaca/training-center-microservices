package com.smalaca.opentrainings.infrastructure.scheduled.offer;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.opentrainings.application.offer.OfferApplicationService;
import com.smalaca.opentrainings.query.offer.OfferQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledOffersTermination {
    private final OfferApplicationService applicationService;
    private final OfferQueryService queryService;

    ScheduledOffersTermination(OfferApplicationService applicationService, OfferQueryService queryService) {
        this.applicationService = applicationService;
        this.queryService = queryService;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.offer.termination.rate}")
    void terminateOffers() {
        queryService.findAllToTerminate().forEach(dto -> applicationService.terminate(dto.getOfferId()));
    }
}