package com.smalaca.opentrainings.domain.offer;

import com.google.common.base.Strings;
import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.discountservice.DiscountCodeDto;
import com.smalaca.opentrainings.domain.discountservice.DiscountResponse;
import com.smalaca.opentrainings.domain.discountservice.DiscountService;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.BeginOfferAcceptanceCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RejectOfferCommand;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.commandid.CommandId.nextAfter;
import static com.smalaca.opentrainings.domain.eventid.EventId.newEventId;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.ACCEPTANCE_IN_PROGRESS;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.DECLINED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.INITIATED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.REJECTED;
import static com.smalaca.opentrainings.domain.offer.OfferStatus.TERMINATED;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent.offerAcceptedEventBuilder;

@AggregateRoot
@Entity
@Table(name = "OFFERS")
public class Offer {
    @Id
    @GeneratedValue
    @Column(name = "OFFER_ID")
    private UUID offerId;

    @Column(name = "TRAINING_ID")
    private UUID trainingId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "OFFER_NUMBER"))
    private OfferNumber offerNumber;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "TRAINING_PRICE_AMOUNT"))
    @AttributeOverride(name = "currency", column = @Column(name = "TRAINING_PRICE_CURRENCY"))
    private Price trainingPrice;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OfferStatus status;

    private Offer(UUID trainingId, OfferNumber offerNumber, Price trainingPrice, LocalDateTime creationDateTime) {
        this.trainingId = trainingId;
        this.offerNumber = offerNumber;
        this.trainingPrice = trainingPrice;
        this.creationDateTime = creationDateTime;
    }

    private Offer() {}

    @Factory
    static Offer initiate(UUID trainingId, OfferNumber offerNumber, Price trainingPrice, LocalDateTime creationDateTime) {
        Offer offer = new Offer(trainingId, offerNumber, trainingPrice, creationDateTime);
        offer.status = INITIATED;
        return offer;
    }

    public OfferEvent beginAcceptance(BeginOfferAcceptanceCommand command, Clock clock) {
        if (status.isFinal()) {
            return NotAvailableOfferAcceptanceRequestedEvent.nextAfter(command, status.name());
        }

        status = ACCEPTANCE_IN_PROGRESS;

        if (isNewerThan10Minutes(clock)) {
            return UnexpiredOfferAcceptanceRequestedEvent.nextAfter(command);
        } else {
            return ExpiredOfferAcceptanceRequestedEvent.nextAfter(command);
        }
    }

    public OfferEvent accept(AcceptOfferCommand command, TrainingOfferCatalogue trainingOfferCatalogue, DiscountService discountService, Clock clock) {
        if (isOfferNotAvailable(clock, trainingOfferCatalogue)) {
            return reject(asRejectOfferCommand("Offer expired"));
        }

        Price finalPrice = finalPrice(command, discountService);

        TrainingBookingResponse booking = trainingOfferCatalogue.book(new TrainingBookingDto(trainingId, command.participantId()));

        if (booking.isFailed()) {
            return reject(asRejectOfferCommand("Training no longer available"));
        }

        status = OfferStatus.ACCEPTED;

        return offerAcceptedEventBuilder()
                .nextAfter(command)
                .withOfferId(offerId)
                .withTrainingId(trainingId)
                .withParticipantId(command.participantId())
                .withTrainingPrice(trainingPrice)
                .withFinalPrice(finalPrice)
                .withDiscountCode(command.discountCode())
                .build();
    }

    private RejectOfferCommand asRejectOfferCommand(String reason) {
        return new RejectOfferCommand(nextAfter(newEventId()), offerId, reason);
    }

    public OfferRejectedEvent reject(RejectOfferCommand command) {
        status = REJECTED;
        return OfferRejectedEvent.nextAfter(command);
    }

    private Price finalPrice(AcceptOfferCommand command, DiscountService discountService) {
        if (hasNoDiscountCode(command)) {
            return trainingPrice;
        }

        DiscountCodeDto discountCodeDto = new DiscountCodeDto(command.participantId(), trainingId, trainingPrice, command.discountCode());
        DiscountResponse discount = discountService.calculatePriceFor(discountCodeDto);

        if (discount.isFailed()) {
            throw new DiscountException(discount.failureReason());
        }

        return discount.newPrice();
    }

    private boolean hasNoDiscountCode(AcceptOfferCommand command) {
        return Strings.isNullOrEmpty(command.discountCode());
    }

    private boolean isOfferNotAvailable(Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        return isOlderThan10Minutes(clock) && trainingPriceChanged(trainingOfferCatalogue);
    }

    private boolean trainingPriceChanged(TrainingOfferCatalogue trainingOfferCatalogue) {
        TrainingDto trainingDto = trainingOfferCatalogue.detailsOf(trainingId);
        return trainingPrice.differentThan(trainingDto.price());
    }

    public void decline() {
        if (status.isFinal()) {
            throw new OfferInFinalStateException(offerId, status);
        }

        status = DECLINED;
    }

    public void terminate(Clock clock) {
        if (status.isFinal()) {
            throw new OfferInFinalStateException(offerId, status);
        }

        if (isNewerThan10Minutes(clock)) {
            throw new AvailableOfferException(offerId);
        }

        status = TERMINATED;
    }

    private boolean isNewerThan10Minutes(Clock clock) {
        return !isOlderThan10Minutes(clock);
    }

    private boolean isOlderThan10Minutes(Clock clock) {
        LocalDateTime now = clock.now();
        LocalDateTime lastAcceptableDateTime = creationDateTime.plusMinutes(10);
        return now.isAfter(lastAcceptableDateTime) && !now.isEqual(lastAcceptableDateTime);
    }

    public UUID offerId() {
        return offerId;
    }
}
