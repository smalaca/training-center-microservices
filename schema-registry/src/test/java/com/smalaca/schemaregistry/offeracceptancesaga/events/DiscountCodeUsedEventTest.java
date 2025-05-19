package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeUsedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateDiscountCodeUsedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingId = UUID.randomUUID();
        String discountCode = faker.lorem().characters(10);
        BigDecimal originalPrice = BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000));
        BigDecimal newPrice = BigDecimal.valueOf(faker.number().randomDouble(2, 50, 100));
        String priceCurrency = faker.currency().code();

        DiscountCodeUsedEvent event = new DiscountCodeUsedEvent(eventId, offerId, participantId, trainingId, discountCode, originalPrice, newPrice, priceCurrency);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
        assertThat(event.trainingId()).isEqualTo(trainingId);
        assertThat(event.discountCode()).isEqualTo(discountCode);
        assertThat(event.originalPrice()).isEqualTo(originalPrice);
        assertThat(event.newPrice()).isEqualTo(newPrice);
        assertThat(event.priceCurrency()).isEqualTo(priceCurrency);
    }
}