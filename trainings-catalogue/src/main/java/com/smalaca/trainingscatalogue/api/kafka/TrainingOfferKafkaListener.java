package com.smalaca.trainingscatalogue.api.kafka;

import com.smalaca.schemaregistry.trainingoffer.events.TrainingOfferPublishedEvent;
import com.smalaca.trainingscatalogue.trainingoffer.TrainingOfferService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrainingOfferKafkaListener {
    private final TrainingOfferService trainingOfferService;

    TrainingOfferKafkaListener(TrainingOfferService trainingOfferService) {
        this.trainingOfferService = trainingOfferService;
    }

    @KafkaListener(
            topics = "${kafka.topics.trainingoffer.events.training-offer-published}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void listen(TrainingOfferPublishedEvent event) {
        trainingOfferService.handle(event);
    }
}