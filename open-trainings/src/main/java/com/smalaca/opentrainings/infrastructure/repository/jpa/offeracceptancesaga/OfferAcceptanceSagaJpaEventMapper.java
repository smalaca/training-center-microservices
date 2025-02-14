package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;

import java.time.LocalDateTime;

class OfferAcceptanceSagaJpaEventMapper {
    private final ObjectMapper objectMapper;

    OfferAcceptanceSagaJpaEventMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OfferAcceptanceSagaJpaEvent create(OfferAcceptanceSagaEvent event, LocalDateTime consumedAt) {
        return new OfferAcceptanceSagaJpaEvent(
                event.eventId().eventId(),
                event.offerId(),
                consumedAt,
                event.getClass().getCanonicalName(),
                asPayload(event));
    }

    private String asPayload(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new InvalidOfferAcceptanceSagaEventException(e);
        }
    }

    OfferAcceptanceSagaEvent asEvent(OfferAcceptanceSagaJpaEvent from) {
        try {
            return objectMapper.readValue(from.getPayload(), (Class<OfferAcceptanceSagaEvent>) Class.forName(from.getType()));
        } catch (JsonProcessingException | ClassNotFoundException exception) {
            throw new InvalidOfferAcceptanceSagaEventException(exception);
        }
    }
}
