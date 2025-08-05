package com.smalaca.reviews.infrastructure.eventregistry.kafka;

import com.smalaca.reviews.domain.eventregistry.EventRegistry;
import com.smalaca.reviews.domain.proposal.events.ProposalApprovedEvent;
import com.smalaca.reviews.domain.proposal.events.ProposalRejectedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.reviews.infrastructure.eventregistry.kafka.ProposalApprovedEventAssertion.assertThatProposalApprovedEvent;
import static com.smalaca.reviews.infrastructure.eventregistry.kafka.ProposalRejectedEventAssertion.assertThatProposalRejectedEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.reviews.events.proposal-approved=" + KafkaEventRegistryIntegrationTest.PROPOSAL_APPROVED_EVENT_TOPIC,
        "kafka.topics.reviews.events.proposal-rejected=" + KafkaEventRegistryIntegrationTest.PROPOSAL_REJECTED_EVENT_TOPIC,
        "kafka.group-id=test-group"
})
@Import(ReviewsEventRegistryTestConsumer.class)
class KafkaEventRegistryIntegrationTest {
    protected static final String PROPOSAL_APPROVED_EVENT_TOPIC = "proposal-approved-event-topic";
    protected static final String PROPOSAL_REJECTED_EVENT_TOPIC = "proposal-rejected-event-topic";
    private static final UUID PROPOSAL_ID = UUID.randomUUID();
    private static final UUID REVIEWER_ID = UUID.randomUUID();
    private static final UUID CORRELATION_ID = UUID.randomUUID();
    private static final LocalDateTime REVIEWED_AT = now();

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private ReviewsEventRegistryTestConsumer consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishProposalApprovedEvent() {
        ProposalApprovedEvent event = ProposalApprovedEvent.create(PROPOSAL_ID, REVIEWER_ID, CORRELATION_ID, REVIEWED_AT);

        eventRegistry.publish(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.reviews.events.ProposalApprovedEvent> actual = consumer.proposalApprovedEventFor(PROPOSAL_ID);
            assertThat(actual).isPresent();

            assertThatProposalApprovedEvent(actual.get())
                    .hasEventIdFrom(event.eventId())
                    .hasProposalId(PROPOSAL_ID)
                    .hasReviewerId(REVIEWER_ID);
        });
    }

    @Test
    void shouldPublishProposalRejectedEvent() {
        ProposalRejectedEvent event = ProposalRejectedEvent.create(PROPOSAL_ID, REVIEWER_ID, CORRELATION_ID, REVIEWED_AT);

        eventRegistry.publish(event);

        await().untilAsserted(() -> {
            Optional<com.smalaca.schemaregistry.reviews.events.ProposalRejectedEvent> actual = consumer.proposalRejectedEventFor(PROPOSAL_ID);
            assertThat(actual).isPresent();

            assertThatProposalRejectedEvent(actual.get())
                    .hasEventIdFrom(event.eventId())
                    .hasProposalId(PROPOSAL_ID)
                    .hasReviewerId(REVIEWER_ID);
        });
    }
}