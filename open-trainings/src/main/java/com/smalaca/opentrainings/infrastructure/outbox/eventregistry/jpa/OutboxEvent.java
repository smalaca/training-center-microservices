package com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;


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
    @Column(name = "IS_PUBLISHED")
    private boolean isPublished;

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

    void published() {
        isPublished = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutboxEvent that = (OutboxEvent) o;
        return isPublished == that.isPublished &&
                eventId.equals(that.eventId) &&
                occurredOn.truncatedTo(SECONDS).equals(that.occurredOn.truncatedTo(SECONDS)) &&
                type.equals(that.type) &&
                payload.equals(that.payload);
    }

    @Override
    public int hashCode() {
        int result = eventId.hashCode();
        result = 31 * result + occurredOn.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Boolean.hashCode(isPublished);
        result = 31 * result + payload.hashCode();
        return result;
    }
}
