package com.smalaca.opentrainings.domain.order;

import com.smalaca.architecture.portsandadapters.PrimaryPort;
import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.order.events.OrderEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status = INITIATED;

    Order(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    private Order() {}

    @PrimaryPort
    public OrderEvent confirm(PaymentGateway paymentGateway) {
        status = REJECTED;
        return OrderRejectedEvent.expired(orderId);
    }
}
