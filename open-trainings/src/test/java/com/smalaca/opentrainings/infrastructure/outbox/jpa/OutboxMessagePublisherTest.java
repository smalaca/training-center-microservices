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
    private final SpringOutboxEventCrudRepository repository = mock(SpringOutboxEventCrudRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OutboxEventPublisher outboxEventPublisher = new OutboxEventPublisherFactory().outboxEventPublisher(publisher, repository, objectMapper);

    private final OutboxEventTestFactory outboxEventTestFactory = new OutboxEventTestFactory(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertToEvent() {
        OutboxMessage outboxMessage = outboxEventTestFactory.createInvalid();
        given(repository.findByIsPublishedFalse()).willReturn(of(outboxMessage));

        assertThrows(InvalidOutboxEventTypeException.class, outboxEventPublisher::publishOutboxEvents);
    }
}