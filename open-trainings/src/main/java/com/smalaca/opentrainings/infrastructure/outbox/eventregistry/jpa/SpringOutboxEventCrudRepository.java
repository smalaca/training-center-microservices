package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import com.smalaca.opentrainings.domain.eventid.EventId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpringOutboxEventCrudRepository extends CrudRepository<OutboxEvent, EventId> {
    List<OutboxEvent> findByIsPublishedFalse();
}
