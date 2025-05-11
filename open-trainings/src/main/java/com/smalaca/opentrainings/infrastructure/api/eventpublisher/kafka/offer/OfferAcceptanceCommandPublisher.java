package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

public class OfferAcceptanceCommandPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Topics topics;

    OfferAcceptanceCommandPublisher(KafkaTemplate<String, Object> kafkaTemplate, Topics topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    @EventListener
    public void consume(RegisterPersonCommand command) {
        kafkaTemplate.send(topics.registerPerson(), command);
    }
}
