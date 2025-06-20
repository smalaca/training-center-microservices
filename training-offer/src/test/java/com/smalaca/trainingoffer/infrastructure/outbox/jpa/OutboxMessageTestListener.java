package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class OutboxMessageTestListener {
    List<TrainingOfferPublishedEvent> trainingOfferPublishedEvents = new ArrayList<>();

    @EventListener
    void listen(TrainingOfferPublishedEvent event) {
        trainingOfferPublishedEvents.add(event);
    }
}