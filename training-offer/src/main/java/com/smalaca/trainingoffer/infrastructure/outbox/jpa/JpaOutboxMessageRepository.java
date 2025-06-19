package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

@DrivenAdapter
class JpaOutboxMessageRepository implements EventRegistry {
    private final SpringOutboxMessageCrudRepository repository;
    private final OutboxMessageMapper outboxMessageMapper;

    JpaOutboxMessageRepository(SpringOutboxMessageCrudRepository repository, OutboxMessageMapper outboxMessageMapper) {
        this.repository = repository;
        this.outboxMessageMapper = outboxMessageMapper;
    }

    @Override
    public void publish(TrainingOfferPublishedEvent event) {
        repository.save(outboxMessageMapper.outboxMessage(event.eventId(), event));
    }
}