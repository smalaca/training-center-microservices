package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OutboxMessageMapperTest {
    private static final Faker FAKER = new Faker();
    private static final Class<OfferAcceptanceRequestedEvent> VALID_MESSAGE_CLASS = OfferAcceptanceRequestedEvent.class;
    private static final String VALID_MESSAGE_TYPE = VALID_MESSAGE_CLASS.getCanonicalName();
    private static final String INVALID_MESSAGE_TYPE = FAKER.code().ean8();

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OutboxMessageMapper mapper = new OutboxMessageMapper(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertEventToJson() throws JsonProcessingException {
        OrderRejectedEvent event = OrderRejectedEvent.expired(randomId());
        JsonProcessingException exception = new JsonGenerationException("DUMMY");
        given(objectMapper.writeValueAsString(event)).willThrow(exception);
        Executable executable = () -> mapper.outboxMessage(event.eventId(), event);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, executable);

        assertThat(actual).hasRootCause(exception);
    }

    @Test
    void shouldThrowInvalidOfferAcceptanceSagaEventExceptionWhenInvalidEventType() {
        OutboxMessage message = randomOutboxMessage(INVALID_MESSAGE_TYPE);
        Executable executable = () -> mapper.message(message);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, executable);

        assertThat(actual).hasCauseInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void shouldThrowInvalidOfferAcceptanceSagaEventExceptionWhenInvalidPayload() throws JsonProcessingException {
        OutboxMessage message = randomOutboxMessage(VALID_MESSAGE_TYPE);
        JsonGenerationException cause = new JsonGenerationException("DUMMY");
        given(objectMapper.readValue(message.getPayload(), VALID_MESSAGE_CLASS)).willThrow(cause);
        Executable executable = () -> mapper.message(message);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, executable);

        assertThat(actual).hasCause(cause);
    }

    private OutboxMessage randomOutboxMessage(String messageType) {
        return new OutboxMessage(randomId(), LocalDateTime.now(), messageType, FAKER.code().isbn10());
    }
}