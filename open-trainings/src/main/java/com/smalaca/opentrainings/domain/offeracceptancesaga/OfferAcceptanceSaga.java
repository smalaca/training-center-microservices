package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.Saga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.COMPLETED;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.IN_PROGRESS;

@Saga
public class OfferAcceptanceSaga {
    private final UUID offerId;
    private final List<ConsumedEvent> events = new ArrayList<>();
    private OfferAcceptanceSagaStatus status = IN_PROGRESS;

    public OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public AcceptOfferCommand accept(OfferAcceptanceRequestedEvent event, Clock clock) {
        process(event, clock.now());
        return event.asAcceptOfferCommand();
    }

    private void process(OfferAcceptanceRequestedEvent event, LocalDateTime consumedAt) {
        ConsumedEvent consumedEvent = new ConsumedEvent(event.eventId(), consumedAt, event);
        events.add(consumedEvent);
        status = COMPLETED;
    }

    public void readLastEvent(BiConsumer<OfferAcceptanceSagaEvent, LocalDateTime> consumer) {
        events.getLast().accept(consumer);
    }

    public void load(OfferAcceptanceSagaEvent event, LocalDateTime consumedAt) {
        event.accept(this, consumedAt);
    }

    public OfferAcceptanceSagaStatus getStatus() {
        return status;
    }
}
