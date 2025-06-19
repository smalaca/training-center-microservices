package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

interface SpringOutboxMessageCrudRepository extends CrudRepository<OutboxMessage, UUID> {
}