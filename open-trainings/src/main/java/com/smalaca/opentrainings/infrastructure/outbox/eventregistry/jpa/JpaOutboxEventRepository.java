package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaEventRegistry;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;

@DrivenAdapter
public class JpaOutboxEventRepository implements EventRegistry, OfferAcceptanceSagaEventRegistry {
    private final SpringOutboxEventCrudRepository repository;
    private final OutboxEventFactory outboxEventFactory;

    JpaOutboxEventRepository(SpringOutboxEventCrudRepository repository, OutboxEventFactory outboxEventFactory) {
        this.repository = repository;
        this.outboxEventFactory = outboxEventFactory;
    }

    @Override
    public void publish(OfferEvent event) {

    }

    @Override
    public void publish(OrderEvent event) {
        publish(event.eventId(), event);
    }

    @Override
    public void publish(OfferAcceptanceSagaEvent event) {
        publish(event.eventId(), event);
    }

    private void publish(EventId eventId, Object event) {
        repository.save(outboxEventFactory.create(eventId, event));
    }

    Iterable<OutboxEvent> findAll() {
        return repository.findAll();
    }
}
