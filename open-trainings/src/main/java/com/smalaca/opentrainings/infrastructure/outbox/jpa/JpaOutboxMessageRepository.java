package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.commandregistry.CommandRegistry;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaEventRegistry;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;

@DrivenAdapter
class JpaOutboxMessageRepository implements EventRegistry, OfferAcceptanceSagaEventRegistry, CommandRegistry {
    private final SpringOutboxMessageCrudRepository repository;
    private final OutboxMessageMapper outboxMessageMapper;

    JpaOutboxMessageRepository(SpringOutboxMessageCrudRepository repository, OutboxMessageMapper outboxMessageMapper) {
        this.repository = repository;
        this.outboxMessageMapper = outboxMessageMapper;
    }

    @Override
    public void publish(OfferEvent event) {
        publish(event.eventId(), event);
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
        repository.save(outboxMessageMapper.outboxMessage(eventId, event));
    }

    @Override
    public void publish(OfferAcceptanceSagaCommand command) {
        repository.save(outboxMessageMapper.outboxMessage(command.commandId(), command));
    }
}
