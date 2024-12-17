package com.smalaca.opentrainings.domain.order;

import com.smalaca.architecture.portsandadapters.PrimaryPort;
import com.smalaca.domaindrivendesign.AggregateRoot;
import com.smalaca.opentrainings.domain.paymentgateway.PaymentGateway;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.order.OrderStatus.CONFIRMED;
import static com.smalaca.opentrainings.domain.order.OrderStatus.INITIATED;

@AggregateRoot
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OrderStatus status = INITIATED;

    private Order() {}

    @PrimaryPort
    public void confirm(PaymentGateway paymentGateway) {
        status = CONFIRMED;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
