package com.smalaca.personaldatamanagement.api;

import com.smalaca.contracts.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.contracts.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.contracts.offeracceptancesaga.events.PersonRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PersonCommandProcessor {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String alreadyRegisteredPersonTopic;
    private final String personRegisteredTopic;
    private final Map<String, UUID> participants = new ConcurrentHashMap<>();

    PersonCommandProcessor(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.event.already-registered-person}") String alreadyRegisteredPersonTopic,
            @Value("${kafka.topics.event.person-registered}") String personRegisteredTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.alreadyRegisteredPersonTopic = alreadyRegisteredPersonTopic;
        this.personRegisteredTopic = personRegisteredTopic;
    }

    @KafkaListener(
            topics = "${kafka.topics.command.register-person}",
            groupId = "${kafka.group-id}",
            containerFactory = "listenerContainerFactory")
    public void process(RegisterPersonCommand command) {
        String fullName = asFullName(command);

        if (participants.containsKey(fullName)) {
            AlreadyRegisteredPersonFoundEvent event = alreadyRegisteredPersonFoundEvent(command, participants.get(fullName));
            kafkaTemplate.send(alreadyRegisteredPersonTopic, event);
        } else {
            participants.put(fullName, registerPerson());
            PersonRegisteredEvent event = personRegisteredEvent(command, participants.get(fullName));
            kafkaTemplate.send(personRegisteredTopic, event);
        }
    }

    private AlreadyRegisteredPersonFoundEvent alreadyRegisteredPersonFoundEvent(RegisterPersonCommand command, UUID participantId) {
        return new AlreadyRegisteredPersonFoundEvent(command.commandId().nextEventId(), command.offerId(), participantId);
    }

    private PersonRegisteredEvent personRegisteredEvent(RegisterPersonCommand command, UUID participantId) {
        return new PersonRegisteredEvent(command.commandId().nextEventId(), command.offerId(), participantId);
    }

    private UUID registerPerson() {
        return UUID.randomUUID();
    }

    private String asFullName(RegisterPersonCommand command) {
        return command.firstName() + command.lastName();
    }
}
