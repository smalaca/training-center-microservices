package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

public class OutboxMessagePublisher {
    private final ApplicationEventPublisher publisher;
    private final SpringOutboxMessageCrudRepository repository;
    private final MessageFactory messageFactory;

    OutboxMessagePublisher(ApplicationEventPublisher publisher, SpringOutboxMessageCrudRepository repository, MessageFactory messageFactory) {
        this.publisher = publisher;
        this.repository = repository;
        this.messageFactory = messageFactory;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.outbox.message.rate}")
    void publishOutboxEvents() {
        repository.findByIsPublishedFalse().forEach(outboxEvent -> {
            publisher.publishEvent(messageFactory.from(outboxEvent));

            outboxEvent.published();
            repository.save(outboxEvent);
        });
    }
}
