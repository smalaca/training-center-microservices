package com.smalaca.trainingscatalogue.trainingprogram;

import com.smalaca.schemaregistry.trainingprogram.events.TrainingProgramReleasedEvent;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.smalaca.schemaregistry.metadata.EventId.newEventId;

public class RandomTrainingProgramFactory {
    private static final Faker FAKER = new Faker();

    public static TrainingProgram randomTrainingProgram() {
        TrainingProgramReleasedEvent event = new TrainingProgramReleasedEvent(
                newEventId(), randomId(), randomId(), randomName(), randomDescription(),
                randomAgenda(), randomPlan(), randomId(), randomId(), randomCategoriesIds());

        return new TrainingProgram(event);
    }

    private static List<UUID> randomCategoriesIds() {
        List<UUID> categoriesIds = new ArrayList<>();
        int count = FAKER.number().numberBetween(1, 5);

        for (int i = 0; i < count; i++) {
            categoriesIds.add(randomId());
        }

        return categoriesIds;
    }

    private static String randomPlan() {
        return FAKER.lorem().paragraph(3);
    }

    private static String randomAgenda() {
        return FAKER.lorem().paragraph(3);
    }

    private static String randomDescription() {
        return FAKER.lorem().paragraph(2);
    }

    private static String randomName() {
        return FAKER.company().name();
    }

    private static UUID randomId() {
        return UUID.randomUUID();
    }
}
