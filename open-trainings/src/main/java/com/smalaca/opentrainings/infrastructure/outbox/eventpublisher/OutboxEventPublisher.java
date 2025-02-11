package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.SpringOutboxEventCrudRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

public class OutboxEventPublisher {
    private final ApplicationEventPublisher publisher;
    private final SpringOutboxEventCrudRepository repository;
    private final EventFactory eventFactory;

    OutboxEventPublisher(ApplicationEventPublisher publisher, SpringOutboxEventCrudRepository repository, EventFactory eventFactory) {
        this.publisher = publisher;
        this.repository = repository;
        this.eventFactory = eventFactory;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.outbox.event.rate}")
    void publishOutboxEvents() {
        repository.findByIsPublishedFalse().forEach(outboxEvent -> {
            publisher.publishEvent(eventFactory.from(outboxEvent));

            outboxEvent.published();
            repository.save(outboxEvent);
        });
    }
}
