package com.smalaca.trainingoffer.infrastructure.outbox;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.springframework.stereotype.Component;

@Component
@DrivenAdapter
public class DummyEventRegistry implements EventRegistry {
    @Override
    public void publish(TrainingOfferPublishedEvent event) {
        System.out.println("Publishing event: " + event);
    }
}