package com.smalaca.opentrainings.query.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "ORDERS")
public class OrderDto {
    @Id
    @Column(name = "ORDER_ID")
    private UUID orderId;

    @Column(name = "STATUS")
    private String status;

    public UUID getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}
