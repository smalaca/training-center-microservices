package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SpringOutboxEventCrudRepository extends CrudRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByIsPublishedFalse();
}
