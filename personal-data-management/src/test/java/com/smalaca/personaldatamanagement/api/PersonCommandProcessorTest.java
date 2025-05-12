package com.smalaca.personaldatamanagement.api;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEventAssertion;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEventAssertion.assertThatAlreadyRegisteredPersonFoundEvent;
import static com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEventAssertion.assertThatPersonRegisteredEvent;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PersonCommandProcessorTest {
    private static final Faker FAKER = new Faker();
    private static final String ALREADY_REGISTERED_PERSON_TOPIC = "already-registered-person-topic";
    private static final String PERSON_REGISTERED_TOPIC = "person-registered-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final PersonCommandProcessor processor = new PersonCommandProcessor(kafkaTemplate, ALREADY_REGISTERED_PERSON_TOPIC, PERSON_REGISTERED_TOPIC);

    @Test
    void shouldPublishPersonRegisteredEventForNewParticipant() {
        RegisterPersonCommand command = registerPersonCommand();

        processor.process(command);

        thenPersonRegisteredEventPublished()
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId();
    }

    private PersonRegisteredEventAssertion thenPersonRegisteredEventPublished() {
        return assertThatPersonRegisteredEvent(publishedPersonRegisteredEvent());
    }

    @Test
    void shouldPublishAlreadyRegisteredPersonFoundEventForExistingParticipant() {
        String firstName = randomFirstName();
        String lastName = randomLastName();
        processor.process(registerPersonCommand(firstName, lastName));
        PersonRegisteredEvent personRegisteredEvent = publishedPersonRegisteredEvent();
        RegisterPersonCommand command = registerPersonCommand(firstName, lastName);

        processor.process(command);

        ArgumentCaptor<AlreadyRegisteredPersonFoundEvent> captor = ArgumentCaptor.forClass(AlreadyRegisteredPersonFoundEvent.class);
        verify(kafkaTemplate).send(eq(ALREADY_REGISTERED_PERSON_TOPIC), captor.capture());

        AlreadyRegisteredPersonFoundEvent event = captor.getValue();
        assertThatAlreadyRegisteredPersonFoundEvent(event)
                .isNextAfter(command.commandId())
                .hasOfferId(command.offerId())
                .hasParticipantId(personRegisteredEvent.participantId());
    }

    private PersonRegisteredEvent publishedPersonRegisteredEvent() {
        ArgumentCaptor<PersonRegisteredEvent> captor = ArgumentCaptor.forClass(PersonRegisteredEvent.class);
        then(kafkaTemplate).should().send(eq(PERSON_REGISTERED_TOPIC), captor.capture());
        return captor.getValue();
    }

    private RegisterPersonCommand registerPersonCommand() {
        return registerPersonCommand(randomFirstName(), randomLastName());
    }

    private RegisterPersonCommand registerPersonCommand(String firstName, String lastName) {
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
