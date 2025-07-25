package com.smalaca.schemaregistry.trainingoffer.events;

import com.smalaca.schemaregistry.metadata.EventId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingPriceChangedEventTest {
    @Test
    void shouldCreateTrainingPriceChangedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID trainingOfferId = UUID.randomUUID();
        BigDecimal priceAmount = BigDecimal.valueOf(123.45);
        String priceCurrencyCode = "USD";

        TrainingPriceChangedEvent event = new TrainingPriceChangedEvent(eventId, offerId, trainingOfferId, priceAmount, priceCurrencyCode);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.trainingOfferId()).isEqualTo(trainingOfferId);
        assertThat(event.priceAmount()).isEqualTo(priceAmount);
        assertThat(event.priceCurrencyCode()).isEqualTo(priceCurrencyCode);
    }
}