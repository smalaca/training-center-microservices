package com.smalaca.opentrainings.infrastructure.eventregistry.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Table(name = "OUTBOX_EVENTS")
public class OutboxEvent {
    @Id
    @Column(name = "EVENT_ID")
    private UUID eventId;
    @Column(name = "OCCURRED_ON")
    private LocalDateTime occurredOn;
    @Column(name = "EVENT_TYPE")
    private String type;
    @Column(name = "IS_PROCESSED")
    private boolean isProcessed;

    @Lob
    @Column(name = "PAYLOAD")
    private String payload;

    OutboxEvent(UUID eventId, LocalDateTime occurredOn, String type, String payload) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.type = type;
        this.payload = payload;
    }

    private OutboxEvent() {}
}
