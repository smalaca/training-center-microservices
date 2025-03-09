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
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeAlreadyUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.DiscountCodeUsedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPlaceBookedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceChangedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.TrainingPriceNotChangedEvent;

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
    private final List<ConsumedEvent> events = new ArrayList<>();
    private String rejectionReason;
    private OfferAcceptanceSagaStatus status;
    private String discountCode;
    private UUID participantId;
    private boolean isOfferPriceConfirmed;

    public OfferAcceptanceSaga(UUID offerId) {
        this.offerId = offerId;
    }

    public List<OfferAcceptanceSagaCommand> accept(OfferAcceptanceRequestedEvent event, Clock clock) {
        status = IN_PROGRESS;
        discountCode = event.discountCode();
        consumed(event, clock.now());

        return asList(
                RegisterPersonCommand.nextAfter(event),
                BeginOfferAcceptanceCommand.nextAfter(event));
    }

    public Optional<AcceptOfferCommand> accept(PersonRegisteredEvent event, Clock clock) {
        consumed(event, clock.now());
        participantId = event.participantId();
        return startBookingIfPossible(event);
    }

    public Optional<AcceptOfferCommand> accept(AlreadyRegisteredPersonFoundEvent event, Clock clock) {
        consumed(event, clock.now());
        participantId = event.participantId();
        return startBookingIfPossible(event);
    }

    public Optional<AcceptOfferCommand> accept(UnexpiredOfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        isOfferPriceConfirmed = true;
        return startBookingIfPossible(event);
    }

    public ConfirmTrainingPriceCommand accept(ExpiredOfferAcceptanceRequestedEvent event, Clock clock) {
        consumed(event, clock.now());
        return ConfirmTrainingPriceCommand.nextAfter(event);
    }

    public Optional<AcceptOfferCommand> accept(TrainingPriceNotChangedEvent event, Clock clock) {
        consumed(event, clock.now());
        isOfferPriceConfirmed = true;
        return startBookingIfPossible(event);
    }

    private Optional<AcceptOfferCommand> startBookingIfPossible(OfferAcceptanceSagaEvent event) {
        if (canStartBooking()) {
            return Optional.of(AcceptOfferCommand.nextAfter(event, participantId, discountCode));
        } else {
            return Optional.empty();
        }
    }

    private boolean canStartBooking() {
        return participantId != null && isOfferPriceConfirmed;
    }

    public RejectOfferCommand accept(TrainingPriceChangedEvent event, Clock clock) {
        consumed(event, clock.now());
        return RejectOfferCommand.nextAfter(event);
    }

    public void accept(DiscountCodeUsedEvent event, Clock clock) {
        consumed(event, clock.now());
    }

    public void accept(DiscountCodeAlreadyUsedEvent event, Clock clock) {
        consumed(event, clock.now());
    }

    public void accept(TrainingPlaceBookedEvent event, Clock clock) {
        consumed(event, clock.now());
    }

    public void accept(NoAvailableTrainingPlacesLeftEvent event, Clock clock) {
        consumed(event, clock.now());
    }

    public void accept(OfferAcceptedEvent event, Clock clock) {
        consumed(event, clock.now());
        status = ACCEPTED;
    }

    public void accept(OfferRejectedEvent event, Clock clock) {
        reject(event, clock, event.reason());
    }

    public void accept(NotAvailableOfferAcceptanceRequestedEvent event, Clock clock) {
        reject(event, clock, "Offer already " + event.status());
    }

    private void reject(OfferAcceptanceSagaEvent event, Clock clock, String rejectionReason) {
        consumed(event, clock.now());
        this.rejectionReason = rejectionReason;
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
