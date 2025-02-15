package com.smalaca.opentrainings.infrastructure.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OutboxMessagePublisherTest {
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final SpringOutboxMessageCrudRepository repository = mock(SpringOutboxMessageCrudRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OutboxMessagePublisher outboxMessagePublisher = new OutboxMessagePublisherFactory().outboxMessagePublisher(publisher, repository, objectMapper);

    private final OutboxMessageTestFactory outboxMessageTestFactory = new OutboxMessageTestFactory(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertToEvent() {
        OutboxMessage outboxMessage = outboxMessageTestFactory.createInvalid();
        given(repository.findByIsPublishedFalse()).willReturn(of(outboxMessage));

        assertThrows(InvalidOutboxMessageTypeException.class, outboxMessagePublisher::publishOutboxEvents);
    }
}