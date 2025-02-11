package com.smalaca.opentrainings.infrastructure.outbox.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.OutboxEvent;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.OutboxEventTestFactory;
import com.smalaca.opentrainings.infrastructure.outbox.eventregistry.jpa.SpringOutboxEventCrudRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OutboxEventPublisherTest {
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final SpringOutboxEventCrudRepository repository = mock(SpringOutboxEventCrudRepository.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final OutboxEventPublisher outboxEventPublisher = new OutboxEventPublisherFactory().outboxEventPublisher(publisher, repository, objectMapper);

    private final OutboxEventTestFactory outboxEventTestFactory = new OutboxEventTestFactory(objectMapper);

    @Test
    void shouldThrowRuntimeExceptionWhenCannotConvertToEvent() {
        OutboxEvent outboxEvent = outboxEventTestFactory.createInvalid();
        given(repository.findByIsPublishedFalse()).willReturn(of(outboxEvent));

        assertThrows(InvalidOutboxEventTypeException.class, outboxEventPublisher::publishOutboxEvents);
    }
}