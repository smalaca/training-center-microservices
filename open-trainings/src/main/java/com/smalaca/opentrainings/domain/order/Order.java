package com.smalaca.opentrainings.domain.order;

import com.smalaca.architecture.portsandadapters.PrimaryPort;
import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
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

import static com.smalaca.opentrainings.domain.order.OrderStatus.CONFIRMED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.INITIATED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.REJECTED;

@AggregateRoot
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private UUID orderId;

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

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status = INITIATED;

    Order(UUID trainingId, UUID participantId, Price price, LocalDateTime creationDateTime) {
        this.trainingId = trainingId;
        this.participantId = participantId;
        this.price = price;
        this.creationDateTime = creationDateTime;
    }

    private Order() {}

    public UUID orderId() {
        return orderId;
    }

    @PrimaryPort
    public OrderEvent confirm(PaymentGateway paymentGateway, Clock clock) {
        if (isOlderThan10Minutes(clock)) {
            status = REJECTED;
            return OrderRejectedEvent.expired(orderId);
        }

        if (paymentGateway.pay(paymentRequest()).isSuccessful()) {
            status = CONFIRMED;
            return TrainingPurchasedEvent.create(orderId, trainingId, participantId);
        } else {
            status = REJECTED;
            return OrderRejectedEvent.paymentFailed(orderId);
        }
    }

    private PaymentRequest paymentRequest() {
        return PaymentRequest.builder()
                .orderId(orderId)
                .participantId(participantId)
                .price(price.amount(), price.currencyCode())
                .build();
    }

    private boolean isOlderThan10Minutes(Clock clock) {
        LocalDateTime now = clock.now();
        LocalDateTime lastAcceptableDateTime = creationDateTime.plusMinutes(10);
        return now.isAfter(lastAcceptableDateTime) && !now.isEqual(lastAcceptableDateTime);
    }
}
