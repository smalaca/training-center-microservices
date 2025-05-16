package com.smalaca.contracts.offeracceptancesaga.events;

import com.smalaca.contracts.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountCodeAlreadyUsedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateDiscountCodeAlreadyUsedEvent() {
        EventId eventId = EventId.newEventId();
        UUID offerId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();
        UUID trainingId = UUID.randomUUID();
        String discountCode = faker.lorem().characters(10);

        DiscountCodeAlreadyUsedEvent event = new DiscountCodeAlreadyUsedEvent(eventId, offerId, participantId, trainingId, discountCode);

        assertThat(event.eventId()).isEqualTo(eventId);
        assertThat(event.offerId()).isEqualTo(offerId);
        assertThat(event.participantId()).isEqualTo(participantId);
        assertThat(event.trainingId()).isEqualTo(trainingId);
        assertThat(event.discountCode()).isEqualTo(discountCode);
    }
}