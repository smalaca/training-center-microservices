package com.smalaca.opentrainings.domain.order;

import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.domaindrivendesign.Factory;
import com.smalaca.opentrainings.domain.clock.Clock;
import com.smalaca.opentrainings.domain.order.events.OrderCancelledEvent;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderTerminatedEvent;
import com.smalaca.opentrainings.domain.order.events.TrainingPurchasedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentRequest;
import com.smalaca.opentrainings.domain.paymentmethod.PaymentMethod;
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
import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderStatus.CANCELLED;
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

    @Column(name = "OFFER_ID")
    private UUID offerId;

    @Column(name = "PARTICIPANT_ID")
    private UUID participantId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "ORDER_NUMBER"))
    })
    private OrderNumber orderNumber;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "TRAINING_PRICE_AMOUNT")),
            @AttributeOverride(name = "currency", column = @Column(name = "TRAINING_PRICE_CURRENCY"))
    })
    private Price trainingPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "FINAL_PRICE_AMOUNT")),
            @AttributeOverride(name = "currency", column = @Column(name = "FINAL_PRICE_CURRENCY"))
    })
    private Price finalPrice;

    @Column(name = "DISCOUNT_CODE")
    private String discountCode;

    private Order() {}

    private Order(Builder builder) {
        offerId = builder.offerId;
        trainingId = builder.trainingId;
        participantId = builder.participantId;
        trainingPrice = builder.trainingPrice;
        finalPrice = builder.finalPrice;
        discountCode = builder.discountCode;
        orderNumber = builder.orderNumber;
        creationDateTime = builder.creationDateTime;
        status = builder.status;
    }

    public UUID orderId() {
        return orderId;
    }

    public OrderEvent confirm(PaymentMethod paymentMethod, PaymentGateway paymentGateway, Clock clock) {
        if (status.isFinal()) {
            throw new OrderInFinalStateException(orderId, status);
        }

        if (isOlderThan10Minutes(clock)) {
            status = REJECTED;
            return OrderRejectedEvent.expired(orderId);
        }

        if (paymentGateway.pay(paymentRequestWith(paymentMethod)).isSuccessful()) {
            status = CONFIRMED;
            return TrainingPurchasedEvent.create(orderId, offerId, trainingId, participantId);
        } else {
            status = REJECTED;
            return OrderRejectedEvent.paymentFailed(orderId);
        }
    }

    private PaymentRequest paymentRequestWith(PaymentMethod paymentMethod) {
        return PaymentRequest.builder()
                .orderId(orderId)
                .participantId(participantId)
                .price(trainingPrice)
                .paymentMethod(paymentMethod)
                .build();
    }

    public OrderCancelledEvent cancel() {
        if (status.isFinal()) {
            throw new OrderInFinalStateException(orderId, status);
        }

        status = CANCELLED;
        return OrderCancelledEvent.create(orderId, offerId, trainingId, participantId);
    }

    public OrderTerminatedEvent terminate(Clock clock) {
        if (status.isFinal()) {
            throw new OrderInFinalStateException(orderId, status);
        }

        if (isNewerThan10Minutes(clock)) {
            throw new OrderTerminationNotYetPermittedException(orderId);
        }

        status = OrderStatus.TERMINATED;
        return OrderTerminatedEvent.create(orderId, offerId, trainingId, participantId);
    }

    private boolean isNewerThan10Minutes(Clock clock) {
        return !isOlderThan10Minutes(clock);
    }

    private boolean isOlderThan10Minutes(Clock clock) {
        LocalDateTime now = clock.now();
        LocalDateTime lastAcceptableDateTime = creationDateTime.plusMinutes(10);
        return now.isAfter(lastAcceptableDateTime) && !now.isEqual(lastAcceptableDateTime);
    }

    @Factory
    static class Builder {
        private UUID offerId;
        private UUID trainingId;
        private UUID participantId;
        private Price trainingPrice;
        private Price finalPrice;
        private String discountCode;
        private OrderNumber orderNumber;
        private LocalDateTime creationDateTime;
        private OrderStatus status = INITIATED;

        Builder withOfferId(UUID offerId) {
            this.offerId = offerId;
            return this;
        }

        Builder withTrainingId(UUID trainingId) {
            this.trainingId = trainingId;
            return this;
        }

        Builder withParticipantId(UUID participantId) {
            this.participantId = participantId;
            return this;
        }

        Builder withTrainingPrice(Price trainingPrice) {
            this.trainingPrice = trainingPrice;
            return this;
        }

        Builder withFinalPrice(Price finalPrice) {
            this.finalPrice = finalPrice;
            return this;
        }

        Builder withDiscountCode(String discountCode) {
            this.discountCode = discountCode;
            return this;
        }

        Builder withOrderNumber(OrderNumber orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        Builder withCreationDateTime(LocalDateTime creationDateTime) {
            this.creationDateTime = creationDateTime;
            return this;
        }

        Order build() {
            return new Order(this);
        }
    }
}
