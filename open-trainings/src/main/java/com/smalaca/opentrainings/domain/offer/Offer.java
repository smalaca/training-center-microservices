package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.commands.AcceptOfferDomainCommand;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingBookingResponse;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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

import static com.smalaca.opentrainings.domain.offer.OfferStatus.REJECTED;

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
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "PRICE_AMOUNT")),
            @AttributeOverride(name = "currency", column = @Column(name = "PRICE_CURRENCY"))
    })
    private Price price;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OfferStatus status;

    public Offer(UUID trainingId, Price price, LocalDateTime creationDateTime) {
        this.trainingId = trainingId;
        this.price = price;
        this.creationDateTime = creationDateTime;
    }

    private Offer() {}

    public OfferEvent accept(
            AcceptOfferDomainCommand command, PersonalDataManagement personalDataManagement,
            TrainingOfferCatalogue trainingOfferCatalogue, Clock clock) {
        if (isOfferNotAvailable(clock, trainingOfferCatalogue)) {
            status = REJECTED;
            return OfferRejectedEvent.expired(offerId);
        }

        PersonalDataResponse response = personalDataManagement.save(command.asPersonalDataRequest());

        if (response.isFailed()) {
            throw new MissingParticipantException();
        }

        TrainingBookingResponse booking = trainingOfferCatalogue.book(new TrainingBookingDto(trainingId, response.participantId()));

        if (booking.isFailed()) {
            status = REJECTED;
            return OfferRejectedEvent.trainingNoLongerAvailable(offerId);
        }

        status = OfferStatus.ACCEPTED;
        return OfferAcceptedEvent.create(offerId, trainingId, response.participantId(), price);
    }

    private boolean isOfferNotAvailable(Clock clock, TrainingOfferCatalogue trainingOfferCatalogue) {
        return isOlderThan10Minutes(clock) && trainingPriceChanged(trainingOfferCatalogue);
    }

    private boolean trainingPriceChanged(TrainingOfferCatalogue trainingOfferCatalogue) {
        Price currentPrice = trainingOfferCatalogue.priceFor(trainingId);
        return price.differentThan(currentPrice);
    }

    private boolean isOlderThan10Minutes(Clock clock) {
        LocalDateTime now = clock.now();
        LocalDateTime lastAcceptableDateTime = creationDateTime.plusMinutes(10);
        return now.isAfter(lastAcceptableDateTime) && !now.isEqual(lastAcceptableDateTime);
    }
}
