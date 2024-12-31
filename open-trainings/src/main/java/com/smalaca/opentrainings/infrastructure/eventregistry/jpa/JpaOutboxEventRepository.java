package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import org.springframework.stereotype.Repository;

@Repository
@DrivenAdapter
public class JpaOutboxEventRepository implements EventRegistry {
    private final SpringOutboxEventCrudRepository repository;
    private final ObjectMapper objectMapper;

    JpaOutboxEventRepository(SpringOutboxEventCrudRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {
        repository.save(asOutboxEvent(event));
    }

    private OutboxEvent asOutboxEvent(OrderEvent event) {
        return new OutboxEvent(
                event.eventId().eventId(),
                event.eventId().creationDateTime(),
                event.getClass().getSimpleName(),
                asPayload(event));
    }

    private String asPayload(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    Iterable<OutboxEvent> findAll() {
        return repository.findAll();
    }
}
