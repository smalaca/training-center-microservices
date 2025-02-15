package com.smalaca.opentrainings.infrastructure.repository.jpa.offeracceptancesaga;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OfferAcceptanceSagaPersistableEventMapperTest {
    private static final Faker FAKER = new Faker();
    private static final Class<OfferAcceptanceRequestedEvent> VALID_EVENT_CLASS = OfferAcceptanceRequestedEvent.class;
    private static final String VALID_EVENT_TYPE = VALID_EVENT_CLASS.getCanonicalName();
    private static final String INVALID_EVENT_TYPE = FAKER.code().ean8();
    private static final UUID OFFER_ID = randomId();
    private static final LocalDateTime CONSUMED_AT = LocalDateTime.now();

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OfferAcceptanceSagaPersistableEventMapper mapper = new OfferAcceptanceSagaPersistableEventMapper(objectMapper);

    @Test
    void shouldThrowInvalidOfferAcceptanceSagaEventExceptionWhenCannotConvertPayload() throws JsonProcessingException {
        OfferAcceptanceRequestedEvent event = randomOfferAcceptanceRequestedEvent();
        JsonGenerationException cause = new JsonGenerationException("DUMMY");
        given(objectMapper.writeValueAsString(event)).willThrow(cause);
        Executable executable = () -> mapper.offerAcceptanceSagaPersistableEventFrom(event, CONSUMED_AT);

        InvalidOfferAcceptanceSagaEventException actual = Assertions.assertThrows(InvalidOfferAcceptanceSagaEventException.class, executable);

        assertThat(actual).hasCause(cause);
    }

    @Test
    void shouldThrowInvalidOfferAcceptanceSagaEventExceptionWhenInvalidEventType() {
        OfferAcceptanceSagaPersistableEvent event = randomOfferAcceptanceSagaPersistableEvent(INVALID_EVENT_TYPE);
        Executable executable = () -> mapper.offerAcceptanceSagaEventFrom(event);

        InvalidOfferAcceptanceSagaEventException actual = Assertions.assertThrows(InvalidOfferAcceptanceSagaEventException.class, executable);

        assertThat(actual).hasCauseInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void shouldThrowInvalidOfferAcceptanceSagaEventExceptionWhenInvalidPayload() throws JsonProcessingException {
        OfferAcceptanceSagaPersistableEvent event = randomOfferAcceptanceSagaPersistableEvent(VALID_EVENT_TYPE);
        JsonGenerationException cause = new JsonGenerationException("DUMMY");
        given(objectMapper.readValue(event.getPayload(), VALID_EVENT_CLASS)).willThrow(cause);
        Executable executable = () -> mapper.offerAcceptanceSagaEventFrom(event);

        InvalidOfferAcceptanceSagaEventException actual = Assertions.assertThrows(InvalidOfferAcceptanceSagaEventException.class, executable);

        assertThat(actual).hasCause(cause);
    }

    private OfferAcceptanceSagaPersistableEvent randomOfferAcceptanceSagaPersistableEvent(String eventType) {
        return new OfferAcceptanceSagaPersistableEvent(randomId(), OFFER_ID, CONSUMED_AT, eventType, FAKER.lorem().paragraph());
    }

    private OfferAcceptanceRequestedEvent randomOfferAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), FAKER.code().imei());
    }
}