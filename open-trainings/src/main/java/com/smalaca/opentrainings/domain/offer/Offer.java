package com.smalaca.opentrainings.domain.offer;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.order.Order;
import com.smalaca.opentrainings.domain.order.OrderFactory;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.order.commands.CreateOrderCommand;
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

import java.util.UUID;

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

    @Column(name = "PARTICIPANT_ID")
    private UUID participantId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "PRICE_AMOUNT")),
            @AttributeOverride(name = "currency", column = @Column(name = "PRICE_CURRENCY"))
    })
    private Price price;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OfferStatus status;

    public Offer(UUID trainingId, UUID participantId, Price price) {
        this.trainingId = trainingId;
        this.participantId = participantId;
        this.price = price;
    }

    private Offer() {}

    public Order accept(OrderFactory orderFactory) {
        status = OfferStatus.ACCEPTED;
        return orderFactory.create(new CreateOrderCommand(trainingId, participantId, price));
    }
}
