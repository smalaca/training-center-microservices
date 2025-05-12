package com.smalaca.personaldatamanagement.api;

import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.RegisterPersonCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.AlreadyRegisteredPersonFoundEvent;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.PersonRegisteredEvent;
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
            AlreadyRegisteredPersonFoundEvent event = command.alreadyRegisteredPersonFoundEvent(participants.get(fullName));
            kafkaTemplate.send(alreadyRegisteredPersonTopic, event);
        } else {
            participants.put(fullName, registerPerson());
            PersonRegisteredEvent event = command.personRegisteredEvent(participants.get(fullName));
            kafkaTemplate.send(personRegisteredTopic, event);
        }
    }

    private UUID registerPerson() {
        return UUID.randomUUID();
    }

    private String asFullName(RegisterPersonCommand command) {
        return command.firstName() + command.lastName();
    }
}
