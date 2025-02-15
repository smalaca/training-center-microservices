package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.domain.order.events.OrderRejectedEvent;
import org.junit.jupiter.api.Test;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class JpaOutboxMessageRepositoryTest {
    private static final SpringOutboxMessageCrudRepository DUMMY_REPOSITORY = null;

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final JpaOutboxMessageRepository repository = new JpaOutboxMessageRepositoryFactory().jpaOutboxMessageRepository(DUMMY_REPOSITORY, objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertEventToJson() throws JsonProcessingException {
        OrderRejectedEvent dummyEvent = OrderRejectedEvent.expired(randomId());
        JsonProcessingException exception = new JsonProcessingException("dummy message") {};
        given(objectMapper.writeValueAsString(dummyEvent)).willThrow(exception);

        InvalidOutboxMessageException actual = assertThrows(InvalidOutboxMessageException.class, () -> repository.publish(dummyEvent));

        assertThat(actual).hasRootCause(exception);
    }
}