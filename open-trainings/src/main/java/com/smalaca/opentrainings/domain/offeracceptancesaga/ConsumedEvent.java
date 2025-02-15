package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

import static java.time.temporal.ChronoUnit.SECONDS;

class ConsumedEvent {
    private final EventId eventId;
    private final LocalDateTime consumedAt;
    private final OfferAcceptanceSagaEvent event;

    ConsumedEvent(EventId eventId, LocalDateTime consumedAt, OfferAcceptanceSagaEvent event) {
        this.eventId = eventId;
        this.consumedAt = consumedAt;
        this.event = event;
    }

    void accept(BiConsumer<OfferAcceptanceSagaEvent, LocalDateTime> consumer) {
        consumer.accept(event, consumedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsumedEvent that = (ConsumedEvent) o;
        return eventId.equals(that.eventId) &&
                consumedAt.truncatedTo(SECONDS).equals(that.consumedAt.truncatedTo(SECONDS)) &&
                event.equals(that.event);
    }

    @Override
    public int hashCode() {
        int result = eventId.hashCode();
        result = 31 * result + consumedAt.hashCode();
        result = 31 * result + event.hashCode();
        return result;
    }
}
