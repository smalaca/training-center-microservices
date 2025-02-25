package com.smalaca.opentrainings.domain.offeracceptancesaga;

import com.smalaca.domaindrivendesign.Saga;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.ConfirmTrainingPriceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.OfferAcceptanceSagaCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.ACCEPTED;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSagaStatus.REJECTED;
import static java.util.Arrays.asList;

@Saga
public class OfferAcceptanceSaga {
    private final UUID offerId;
    private String rejectionReason;
    private final List<ConsumedEvent> events = new ArrayList<>();
    private OfferAcceptanceSagaStatus status = IN_PROGRESS;
    private String discountCode;
    private UUID participantId;
    private boolean isOfferAcceptanceInProgress;

    public OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public List<OfferAcceptanceSagaCommand> accept(OfferAcceptanceRequestedEvent event, Clock clock) {
        discountCode = event.discountCode();
        consumed(event, clock.now());

        return asList(
                RegisterPersonCommand.nextAfter(event),
                BeginOfferAcceptanceCommand.nextAfter(event));
    }

    public Optional<AcceptOfferCommand> accept(PersonRegisteredEvent event, Clock clock) {
        consumed(event, clock.now());
        participantId = event.participantId();

        if (isOfferAcceptanceInProgress) {
            return Optional.of(AcceptOfferCommand.nextAfter(event, discountCode));
        } else {
            return Optional.empty();
        }
    }

    public Optional<AcceptOfferCommand> accept(AlreadyRegisteredPersonFoundEvent event, Clock clock) {
        consumed(event, clock.now());
        participantId = event.participantId();

        if (isOfferAcceptanceInProgress) {
            return Optional.of(AcceptOfferCommand.nextAfter(event, discountCode));
        } else {
            return Optional.empty();
        }
    }

    public Optional<AcceptOfferCommand> accept(UnexpiredOfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        isOfferAcceptanceInProgress = true;

        if (hasParticipantId()) {
            return Optional.of(AcceptOfferCommand.nextAfter(event, participantId, discountCode));
        } else {
            return Optional.empty();
        }

    }

    private boolean hasParticipantId() {
        return participantId != null;
    }

    public ConfirmTrainingPriceCommand accept(ExpiredOfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        return ConfirmTrainingPriceCommand.nextAfter(event);
    }

    public void accept(NotAvailableOfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        rejectionReason = "Offer already " + event.status();
        status = REJECTED;
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
        return new OfferAcceptanceSagaDto(offerId, status.name(), rejectionReason);
    }
}
