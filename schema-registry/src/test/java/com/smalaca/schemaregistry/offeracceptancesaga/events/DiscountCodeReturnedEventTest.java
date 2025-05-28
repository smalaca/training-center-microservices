package com.smalaca.schemaregistry.offeracceptancesaga.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeReturnedEventTest {
    private static final Faker FAKER = new Faker();

    @Test
    void shouldCreateDiscountCodeReturnedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        String discountCode = FAKER.commerce().promotionCode();

        DiscountCodeReturnedEvent event = new DiscountCodeReturnedEvent(eventId, offerId, participantId, discountCode);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
        assertThat(event.discountCode()).isEqualTo(discountCode);
    }
}