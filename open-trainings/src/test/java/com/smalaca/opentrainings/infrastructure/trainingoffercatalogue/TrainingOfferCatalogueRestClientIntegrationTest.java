package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest(properties = "services.trainings-catalogue.url=http://localhost:1235")
@WireMockTest(httpPort = 1235)
class TrainingOfferCatalogueRestClientIntegrationTest {
    private static final UUID TRAINING_ID = UUID.randomUUID();
    private static final int AVAILABLE_PLACES = 10;
    private static final BigDecimal PRICE_AMOUNT = BigDecimal.valueOf(100);
    private static final String PRICE_CURRENCY = "PLN";

    @Autowired
    private TrainingOfferCatalogue trainingOfferCatalogue;

    @Test
    void shouldReturnTrainingDetailsFromCatalogue() {
        givenTrainingOfferResponse();

        TrainingDto trainingDto = trainingOfferCatalogue.detailsOf(TRAINING_ID);

        assertThat(trainingDto.trainingId()).isEqualTo(TRAINING_ID);
        assertThat(trainingDto.availablePlaces()).isEqualTo(AVAILABLE_PLACES);
        assertThat(trainingDto.price()).isEqualTo(Price.of(PRICE_AMOUNT, PRICE_CURRENCY));
    }

    private void givenTrainingOfferResponse() {
        ResponseDefinitionBuilder response = aResponse().withHeader("Content-Type", "application/json").withBody("""
                {
                    "trainingOfferId": "%s",
                    "trainerId": "%s",
                    "trainingProgramId": "%s",
                    "startDate": "2025-08-01",
                    "endDate": "2025-08-02",
                    "startTime": "09:00:00",
                    "endTime": "17:00:00",
                    "priceAmount": %s,
                    "priceCurrency": "%s",
                    "availablePlaces": %d,
                    "name": "Test Training",
                    "agenda": "Test Agenda",
                    "plan": "Test Plan",
                    "description": "Test Description"
                }
                """.formatted(TRAINING_ID, UUID.randomUUID(), UUID.randomUUID(), PRICE_AMOUNT, PRICE_CURRENCY, AVAILABLE_PLACES));

        stubFor(get(urlEqualTo("/trainingoffers/" + TRAINING_ID)).willReturn(response));
    }
}