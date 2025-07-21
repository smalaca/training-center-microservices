package com.smalaca.trainingoffer.infrastructure.api.eventlistener.spring;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.trainingoffer.application.trainingofferdraft.TrainingOfferDraftApplicationService;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TrainingOfferDraftListener {
    private final TrainingOfferDraftApplicationService trainingOfferDraftApplicationService;

    TrainingOfferDraftListener(TrainingOfferDraftApplicationService trainingOfferDraftApplicationService) {
        this.trainingOfferDraftApplicationService = trainingOfferDraftApplicationService;
    }

    @EventListener
    @DrivingAdapter
    public void listen(TrainingOfferPublishedEvent event) {
        trainingOfferDraftApplicationService.published(event);
    }
}