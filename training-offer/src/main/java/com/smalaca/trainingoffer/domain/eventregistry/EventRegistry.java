package com.smalaca.trainingoffer.domain.eventregistry;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

@DrivenPort
public interface EventRegistry {
    void publish(TrainingOfferPublishedEvent event);
    
    void publish(TrainingOfferEvent event);
}