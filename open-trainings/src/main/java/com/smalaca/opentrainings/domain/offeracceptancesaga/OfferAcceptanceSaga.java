package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.Saga;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.REJECTED;

@Saga
public class OfferAcceptanceSaga {
    private final UUID offerId;
    private UUID orderId;
    private String rejectionReason;
    private final List<ConsumedEvent> events = new ArrayList<>();
    private OfferAcceptanceSagaStatus status = IN_PROGRESS;

    public OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public AcceptOfferCommand accept(OfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        return AcceptOfferCommand.nextAfter(event);
    }

    public void accept(OfferAcceptedEvent event, Clock clock) {
        consumed(event, clock.now());
        status = ACCEPTED;
    }

    public void accept(OfferRejectedEvent event, Clock clock) {
        consumed(event, clock.now());
        rejectionReason = event.reason();
        status = REJECTED;
    }

    private void consumed(OfferAcceptanceSagaEvent event, LocalDateTime consumedAt) {
        ConsumedEvent consumedEvent = new ConsumedEvent(event.eventId(), consumedAt, event);
        events.add(consumedEvent);
    }

    public void readLastEvent(BiConsumer<OfferAcceptanceSagaEvent, LocalDateTime> consumer) {
        events.getLast().accept(consumer);
    }

    public void load(OfferAcceptanceSagaEvent event, LocalDateTime consumedAt) {
        event.accept(this, consumedAt);
    }

    public OfferAcceptanceSagaDto asDto() {
        return new OfferAcceptanceSagaDto(offerId, orderId, status.name(), rejectionReason);
    }
}
