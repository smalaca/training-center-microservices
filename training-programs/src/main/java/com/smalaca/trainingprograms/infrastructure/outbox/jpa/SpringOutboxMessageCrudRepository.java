package com.smalaca.trainingprograms.infrastructure.outbox.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

interface SpringOutboxMessageCrudRepository extends CrudRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findByIsPublishedFalse();
}