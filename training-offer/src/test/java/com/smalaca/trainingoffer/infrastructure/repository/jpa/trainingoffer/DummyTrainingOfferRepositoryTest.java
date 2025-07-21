package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer;

import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DummyTrainingOfferRepositoryTest {
    private final TrainingOfferRepository repository = new DummyTrainingOfferRepository();

    @Test
    void shouldReturnNull() {
        UUID actual = repository.save(null);

        assertThat(actual).isNull();
    }
}