package com.smalaca.opentrainings.infrastructure.api.eventpublisher.kafka.offer;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceRequestedEvent;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.Optional;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@Import(OfferAcceptanceTestKafkaListener.class)
class OfferAcceptanceCommandPublisherIntegrationTest {
    private static final Faker FAKER = new Faker();

    @Autowired
    private OfferAcceptanceCommandPublisher publisher;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private OfferAcceptanceTestKafkaListener consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishRegisterPersonCommand() {
        RegisterPersonCommand command = registerPersonCommand();

        publisher.consume(command);

        await().untilAsserted(() -> {
            Optional<com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand> actual = consumer.registerPersonCommandFor(command.offerId());
            assertThat(actual).isPresent();
            assertThatContainsSameData(actual.get(), command);
        });
    }

    private void assertThatContainsSameData(com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand actual, RegisterPersonCommand expected) {
        assertThatContainsSameData(actual.commandId(), expected.commandId());
        assertThat(actual.offerId()).isEqualTo(expected.offerId());
        assertThat(actual.firstName()).isEqualTo(expected.firstName());
        assertThat(actual.lastName()).isEqualTo(expected.lastName());
        assertThat(actual.email()).isEqualTo(expected.email());
    }

    private void assertThatContainsSameData(com.smalaca.contracts.metadata.CommandId actual, CommandId expected) {
        assertThat(actual.commandId()).isEqualTo(expected.commandId());
        assertThat(actual.traceId()).isEqualTo(expected.traceId());
        assertThat(actual.correlationId()).isEqualTo(expected.correlationId());
        assertThat(actual.creationDateTime()).isEqualTo(expected.creationDateTime());
    }

    private RegisterPersonCommand registerPersonCommand() {
        return RegisterPersonCommand.nextAfter(offerAcceptanceRequestedEvent());
    }

    private OfferAcceptanceRequestedEvent offerAcceptanceRequestedEvent() {
        return OfferAcceptanceRequestedEvent.create(randomId(), FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress(), randomDiscountCode());
    }

    private String randomDiscountCode() {
        return FAKER.code().ean13();
    }
}