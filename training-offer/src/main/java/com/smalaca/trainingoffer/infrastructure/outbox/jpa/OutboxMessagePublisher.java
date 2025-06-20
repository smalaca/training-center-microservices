package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.architecture.portsandadapters.DrivingAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

class OutboxMessagePublisher {
    private final ApplicationEventPublisher publisher;
    private final SpringOutboxMessageCrudRepository repository;
    private final OutboxMessageMapper mapper;

    OutboxMessagePublisher(ApplicationEventPublisher publisher, SpringOutboxMessageCrudRepository repository, OutboxMessageMapper mapper) {
        this.publisher = publisher;
        this.repository = repository;
        this.mapper = mapper;
    }

    @DrivingAdapter
    @Scheduled(fixedRateString = "${scheduled.outbox.message.rate}")
    void publishOutboxEvents() {
        repository.findByIsPublishedFalse().forEach(outboxEvent -> {
            publisher.publishEvent(mapper.message(outboxEvent));

            outboxEvent.published();
            repository.save(outboxEvent);
        });
    }
}