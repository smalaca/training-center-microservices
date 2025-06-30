package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramProposedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateTrainingProgramProposedEvent() {
        EventId eventId = EventId.newEventId();
        UUID trainingProgramProposalId = UUID.randomUUID();
        String name = faker.company().name();
        String description = faker.lorem().paragraph();
        String agenda = faker.lorem().paragraph();
        String plan = faker.lorem().paragraph();
        UUID authorId = UUID.randomUUID();
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        TrainingProgramProposedEvent actual = new TrainingProgramProposedEvent(
                eventId, trainingProgramProposalId, name, description, agenda, plan, authorId, categoriesIds);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
        assertThat(actual.name()).isEqualTo(name);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.agenda()).isEqualTo(agenda);
        assertThat(actual.plan()).isEqualTo(plan);
        assertThat(actual.authorId()).isEqualTo(authorId);
        assertThat(actual.categoriesIds()).isEqualTo(categoriesIds);
    }
}