package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.DomainEntity;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

@DomainEntity
class AcceptedEvent {
    private final EventId eventId;
    private final LocalDateTime consumedAt;
    private final OfferAcceptanceSagaEvent event;

    AcceptedEvent(EventId eventId, LocalDateTime consumedAt, OfferAcceptanceSagaEvent event) {
        this.eventId = eventId;
        this.consumedAt = consumedAt;
        this.event = event;
    }

    void accept(BiConsumer<OfferAcceptanceSagaEvent, LocalDateTime> consumer) {
        consumer.accept(event, consumedAt);
    }
}
