package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.offer.commands.AcceptOfferDomainCommand;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderDomainCommand;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataManagement;
import com.smalaca.opentrainings.domain.personaldatamanagement.PersonalDataResponse;
import com.smalaca.opentrainings.domain.price.Price;
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
import java.util.Optional;
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

    public Optional<Order> accept(
            AcceptOfferDomainCommand command, OrderFactory orderFactory, PersonalDataManagement personalDataManagement, Clock clock) {
        if (isOlderThan10Minutes(clock)) {
            status = REJECTED;
            return Optional.empty();
        }

        PersonalDataResponse response = personalDataManagement.save(command.asPersonalDataRequest());

        if (response.isFailed()) {
            throw new MissingParticipantException();
        }

        return accept(response.participantId(), orderFactory);
    }

    private boolean isOlderThan10Minutes(Clock clock) {
        LocalDateTime now = clock.now();
        LocalDateTime lastAcceptableDateTime = creationDateTime.plusMinutes(10);
        return now.isAfter(lastAcceptableDateTime) && !now.isEqual(lastAcceptableDateTime);
    }

    private Optional<Order> accept(UUID participantId, OrderFactory orderFactory) {
        status = OfferStatus.ACCEPTED;
        CreateOrderDomainCommand command = createOrderCommandWith(participantId);
        return Optional.of(orderFactory.create(command));
    }

    private CreateOrderDomainCommand createOrderCommandWith(UUID participantId) {
        return new CreateOrderDomainCommand(offerId, trainingId, participantId, price);
    }
}
