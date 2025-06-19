package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

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
@Table(name = "OUTBOX_MESSAGES")
class OutboxMessage {
    @Id
    @Column(name = "MESSAGE_ID")
    private UUID messageId;
    @Column(name = "OCCURRED_ON")
    private LocalDateTime occurredOn;
    @Column(name = "MESSAGE_TYPE")
    private String messageType;
    @Column(name = "IS_PUBLISHED")
    private boolean isPublished;

    @Lob
    @Column(name = "PAYLOAD")
    private String payload;

    OutboxMessage(UUID messageId, LocalDateTime occurredOn, String messageType, String payload) {
        this.messageId = messageId;
        this.occurredOn = occurredOn;
        this.messageType = messageType;
        this.payload = payload;
    }

    private OutboxMessage() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutboxMessage that = (OutboxMessage) o;
        return isPublished == that.isPublished &&
                messageId.equals(that.messageId) &&
                occurredOn.truncatedTo(SECONDS).equals(that.occurredOn.truncatedTo(SECONDS)) &&
                messageType.equals(that.messageType) &&
                payload.equals(that.payload);
    }

    @Override
    public int hashCode() {
        int result = messageId.hashCode();
        result = 31 * result + occurredOn.hashCode();
        result = 31 * result + messageType.hashCode();
        result = 31 * result + Boolean.hashCode(isPublished);
        result = 31 * result + payload.hashCode();
        return result;
    }
}
