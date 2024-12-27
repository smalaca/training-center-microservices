package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import com.smalaca.opentrainings.domain.eventid.EventId;
import org.springframework.data.repository.CrudRepository;

public interface SpringOutboxEventCrudRepository extends CrudRepository<OutboxEvent, EventId> {
}
