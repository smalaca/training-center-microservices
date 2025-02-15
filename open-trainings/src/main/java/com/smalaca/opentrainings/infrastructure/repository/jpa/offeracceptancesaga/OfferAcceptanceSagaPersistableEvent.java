package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

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
@Table(name = "OFFER_ACCEPTANCE_SAGA_EVENTS")
class OfferAcceptanceSagaPersistableEvent {
    @Id
    @Column(name = "EVENT_ID")
    private UUID eventId;
    @Column(name = "OFFER_ID")
    private UUID offerId;
    @Column(name = "CONSUMED_AT")
    private LocalDateTime consumedAt;
    @Column(name = "EVENT_TYPE")
    private String type;
    @Lob
    @Column(name = "PAYLOAD")
    private String payload;

    private OfferAcceptanceSagaPersistableEvent() {}

    OfferAcceptanceSagaPersistableEvent(UUID eventId, UUID offerId, LocalDateTime consumedAt, String type, String payload) {
        this.eventId = eventId;
        this.offerId = offerId;
        this.consumedAt = consumedAt;
        this.type = type;
        this.payload = payload;
    }
}
