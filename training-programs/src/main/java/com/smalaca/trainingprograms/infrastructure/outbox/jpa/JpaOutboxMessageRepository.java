package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.trainingprograms.domain.eventregistry.EventRegistry;
import com.smalaca.trainingprograms.domain.trainingprogramproposal.events.TrainingProgramProposalEvent;

@DrivenAdapter
class JpaOutboxMessageRepository implements EventRegistry {
    private final SpringOutboxMessageCrudRepository repository;
    private final OutboxMessageMapper outboxMessageMapper;

    JpaOutboxMessageRepository(SpringOutboxMessageCrudRepository repository, OutboxMessageMapper outboxMessageMapper) {
        this.repository = repository;
        this.outboxMessageMapper = outboxMessageMapper;
    }

    @Override
    public void publish(TrainingProgramProposalEvent event) {
        repository.save(outboxMessageMapper.outboxMessage(event.eventId(), event));
    }
}
