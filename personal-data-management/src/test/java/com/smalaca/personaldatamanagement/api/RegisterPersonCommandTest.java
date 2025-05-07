package com.smalaca.personaldatamanagement.api;

import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.smalaca.personaldatamanagement.api.AlreadyRegisteredPersonFoundEventAssertion.assertThatAlreadyRegisteredPersonFoundEvent;
import static com.smalaca.personaldatamanagement.api.PersonRegisteredEventAssertion.assertThatPersonRegisteredEvent;
import static java.time.LocalDateTime.now;

class RegisterPersonCommandTest {
    private static final Faker FAKER = new Faker();
    private static final CommandId COMMAND_ID = new CommandId(randomId(), randomId(), randomId(), now());
    private static final UUID OFFER_ID = randomId();
    private static final UUID PARTICIPANT_ID = randomId();

    @Test
    void shouldCreateAlreadyRegisteredPersonFoundEvent() {
        AlreadyRegisteredPersonFoundEvent event = registerPersonCommand().alreadyRegisteredPersonFoundEvent(PARTICIPANT_ID);

        assertThatAlreadyRegisteredPersonFoundEvent(event)
            .isNextAfter(COMMAND_ID)
            .hasOfferId(OFFER_ID)
            .hasParticipantId(PARTICIPANT_ID);
    }

    @Test
    void shouldCreatePersonRegisteredEvent() {
        PersonRegisteredEvent event = registerPersonCommand().personRegisteredEvent(PARTICIPANT_ID);

        assertThatPersonRegisteredEvent(event)
                .isNextAfter(COMMAND_ID)
                .hasOfferId(OFFER_ID)
                .hasParticipantId(PARTICIPANT_ID);
    }

    private RegisterPersonCommand registerPersonCommand() {
        return new RegisterPersonCommand(COMMAND_ID, OFFER_ID, FAKER.name().firstName(), FAKER.name().lastName(), FAKER.internet().emailAddress());
    }

    private static UUID randomId() {
        return UUID.randomUUID();
    }
}
