package com.smalaca.reviews.infrastructure.trainerscatalogue;

import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FakeTrainersCatalogueTest {
    private final TrainersCatalogue trainersCatalogue = new FakeTrainersCatalogue();

    @Test
    void shouldFindNoTrainers() {
        List<Trainer> actual = trainersCatalogue.findAllTrainers();

        assertThat(actual).isEmpty();
    }
}