package com.smalaca.schemaregistry.trainingprogram.events;

import com.smalaca.schemaregistry.metadata.EventId;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramReleasedEventTest {
    private final Faker faker = new Faker();

    @Test
    void shouldCreateTrainingProgramReleasedEvent() {
        EventId eventId = EventId.newEventId();
        UUID trainingProgramProposalId = UUID.randomUUID();
        UUID trainingProgramId = UUID.randomUUID();
        String name = faker.company().name();
        String description = faker.lorem().paragraph();
        String agenda = faker.lorem().paragraph();
        String plan = faker.lorem().paragraph();
        UUID authorId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        List<UUID> categoriesIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        TrainingProgramReleasedEvent actual = new TrainingProgramReleasedEvent(
                eventId, trainingProgramProposalId, trainingProgramId, name, description, agenda, plan, authorId, reviewerId, categoriesIds);

        assertThat(actual.eventId()).isEqualTo(eventId);
        assertThat(actual.trainingProgramProposalId()).isEqualTo(trainingProgramProposalId);
        assertThat(actual.trainingProgramId()).isEqualTo(trainingProgramId);
        assertThat(actual.name()).isEqualTo(name);
        assertThat(actual.description()).isEqualTo(description);
        assertThat(actual.agenda()).isEqualTo(agenda);
        assertThat(actual.plan()).isEqualTo(plan);
        assertThat(actual.authorId()).isEqualTo(authorId);
        assertThat(actual.reviewerId()).isEqualTo(reviewerId);
        assertThat(actual.categoriesIds()).isEqualTo(categoriesIds);
    }
}