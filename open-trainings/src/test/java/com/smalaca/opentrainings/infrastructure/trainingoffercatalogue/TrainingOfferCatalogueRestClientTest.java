package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.smalaca.opentrainings.data.Random.randomId;
import static org.assertj.core.api.Assertions.assertThat;

class TrainingOfferCatalogueRestClientTest {
    private final TrainingOfferCatalogueRestClient client = new TrainingOfferCatalogueRestClient();

    @Test
    void shouldReturnTrainingDetails() {
        TrainingDto trainingDto = client.detailsOf(randomId());

        assertThat(trainingDto.availablePlaces()).isEqualTo(42);
        assertThat(trainingDto.price()).isEqualTo(Price.of(BigDecimal.valueOf(100), "PLN"));
    }
}