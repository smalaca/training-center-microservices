package com.smalaca.personaldatamanagement.api;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.personaldatamanagement.api.PersonalDataManagementPivotalEventTestConfiguration.PersonalDataManagementPivotalEventTestConsumer;
import com.smalaca.test.type.SpringBootIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEventAssertion.assertThatAlreadyRegisteredPersonFoundEvent;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEventAssertion.assertThatPersonRegisteredEvent;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootIntegrationTest
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "kafka.bootstrap-servers")
@TestPropertySource(properties = {
        "kafka.topics.command.register-person=" + PersonCommandProcessorIntegrationTest.REGISTER_PERSON_COMMAND_TOPIC,
        "kafka.topics.event.already-registered-person=" + PersonCommandProcessorIntegrationTest.ALREADY_REGISTERED_PERSON_EVENT_TOPIC,
        "kafka.topics.event.person-registered=" + PersonCommandProcessorIntegrationTest.PERSON_REGISTERED_EVENT_TOPIC
})
@Import(PersonalDataManagementPivotalEventTestConfiguration.class)
class PersonCommandProcessorIntegrationTest {
    private static final Faker FAKER = new Faker();
    protected static final String REGISTER_PERSON_COMMAND_TOPIC = "register-person-command-topic";
    protected static final String ALREADY_REGISTERED_PERSON_EVENT_TOPIC = "already-registered-person-event-topic";
    protected static final String PERSON_REGISTERED_EVENT_TOPIC = "person-registered-event-topic";

    @Autowired
    private KafkaTemplate<String, Object> producerFactory;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private PersonalDataManagementPivotalEventTestConsumer consumer;

    @BeforeEach
    void init() {
        kafkaListenerEndpointRegistry.getAllListenerContainers().forEach(
                listenerContainer -> ContainerTestUtils.waitForAssignment(listenerContainer, 1));
    }

    @Test
    void shouldPublishPersonRegisteredEvent() {
        RegisterPersonCommand command = registerPersonCommand();

        producerFactory.send(REGISTER_PERSON_COMMAND_TOPIC, command);

        await().untilAsserted(() -> {
            Optional<PersonRegisteredEvent> actual = consumer.personRegisteredEventFor(command.offerId());
            assertThat(actual).isPresent();

            assertThatPersonRegisteredEvent(actual.get())
                    .isNextAfter(command.commandId())
                    .hasOfferId(command.offerId())
                    .hasParticipantId();
        });
    }

    @Test
    void shouldPublishAlreadyRegisteredPersonFoundEventWhenPersonAlreadyExists() {
        String firstName = randomFirstName();
        String lastName = randomLastName();
        RegisterPersonCommand newPersonCommand = registerPersonCommandFor(firstName, lastName);
        producerFactory.send(REGISTER_PERSON_COMMAND_TOPIC, newPersonCommand);
        RegisterPersonCommand registeredPersonCommand = registerPersonCommandFor(firstName, lastName);

        producerFactory.send(REGISTER_PERSON_COMMAND_TOPIC, registeredPersonCommand);

        await().untilAsserted(() -> {
            Optional<AlreadyRegisteredPersonFoundEvent> actual = consumer.alreadyRegisteredPersonFoundEventFor(registeredPersonCommand.offerId());
            assertThat(actual).isPresent();

            UUID expectedParticipantId = consumer.personRegisteredEventFor(newPersonCommand.offerId()).get().participantId();

            assertThatAlreadyRegisteredPersonFoundEvent(actual.get())
                    .isNextAfter(registeredPersonCommand.commandId())
                    .hasOfferId(registeredPersonCommand.offerId())
                    .hasParticipantId(expectedParticipantId);
        });
    }

    private RegisterPersonCommand registerPersonCommand() {
        return registerPersonCommandFor(randomFirstName(), randomLastName());
    }

    private RegisterPersonCommand registerPersonCommandFor(String firstName, String lastName) {
        CommandId commandId = new CommandId(randomId(), randomId(), randomId(), now());
        return new RegisterPersonCommand(commandId, randomId(), firstName, lastName, FAKER.internet().emailAddress());
    }

    private String randomFirstName() {
        return FAKER.name().firstName();
    }

    private String randomLastName() {
        return FAKER.name().lastName();
    }

    private static UUID randomId() {
        return UUID.randomUUID();
    }
}
