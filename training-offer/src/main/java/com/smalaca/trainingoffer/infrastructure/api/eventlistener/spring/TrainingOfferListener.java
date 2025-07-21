package com.smalaca.trainingoffer.infrastructure.api.eventlistener.spring;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingoffer.application.trainingoffer.TrainingOfferApplicationService;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingOfferListener {
    private final TrainingOfferApplicationService trainingOfferApplicationService;

    TrainingOfferListener(TrainingOfferApplicationService trainingOfferApplicationService) {
        this.trainingOfferApplicationService = trainingOfferApplicationService;
    }

    @EventListener
    @DrivingAdapter
    public void listen(TrainingOfferPublishedEvent event) {
        trainingOfferApplicationService.create(event);
    }
}