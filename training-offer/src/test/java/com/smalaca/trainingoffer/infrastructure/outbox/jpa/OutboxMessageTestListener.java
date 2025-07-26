package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class OutboxMessageTestListener {
    List<TrainingOfferPublishedEvent> trainingOfferPublishedEvents = new ArrayList<>();
    List<TrainingPriceNotChangedEvent> trainingPriceNotChangedEvents = new ArrayList<>();
    List<TrainingPriceChangedEvent> trainingPriceChangedEvents = new ArrayList<>();
    List<NoAvailableTrainingPlacesLeftEvent> noAvailableTrainingPlacesLeftEvents = new ArrayList<>();
    List<TrainingPlaceBookedEvent> trainingPlaceBookedEvents = new ArrayList<>();

    @EventListener
    void listen(TrainingOfferPublishedEvent event) {
        trainingOfferPublishedEvents.add(event);
    }
    
    @EventListener
    void listen(TrainingPriceNotChangedEvent event) {
        trainingPriceNotChangedEvents.add(event);
    }
    
    @EventListener
    void listen(TrainingPriceChangedEvent event) {
        trainingPriceChangedEvents.add(event);
    }
    
    @EventListener
    void listen(NoAvailableTrainingPlacesLeftEvent event) {
        noAvailableTrainingPlacesLeftEvents.add(event);
    }
    
    @EventListener
    void listen(TrainingPlaceBookedEvent event) {
        trainingPlaceBookedEvents.add(event);
    }
}