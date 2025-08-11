package com.smalaca.reviews.infrastructure.trainerscatalogue;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import com.smalaca.test.type.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest(properties = "services.trainers-catalogue.url=http://localhost:1234")
@WireMockTest(httpPort = 1234)
class TrainersCatalogueRestClientIntegrationTest {
    private static final UUID TRAINER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID CATEGORY_ONE = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID CATEGORY_TWO = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private static final UUID ASSIGNMENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    @Autowired
    private TrainersCatalogue trainersCatalogue;

    @Test
    void shouldReturnAllTrainersFromTrainersCatalogue() {
        givenTrainersResponse();

        List<Trainer> trainers = trainersCatalogue.findAllTrainers();

        assertThat(trainers)
                .hasSize(1)
                .anySatisfy(trainer -> {
                    assertThat(trainer.id()).isEqualTo(TRAINER_ID);
                    assertThat(trainer.categoryIds()).containsExactlyInAnyOrder(CATEGORY_ONE, CATEGORY_TWO);
                    assertThat(trainer.assignmentsIds()).containsExactlyInAnyOrder(ASSIGNMENT_ID);
                });
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainersFound() {
        givenEmptyTrainersResponse();

        List<Trainer> trainers = trainersCatalogue.findAllTrainers();

        assertThat(trainers).isEmpty();
    }

    private void givenTrainersResponse() {
        ResponseDefinitionBuilder response = aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    [
                        {
                            "id": "550e8400-e29b-41d4-a716-446655440000",
                            "categoryIds": ["550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002"],
                            "assignmentsIds": ["550e8400-e29b-41d4-a716-446655440003"]
                        }
                    ]
                    """);

        stubFor(get(urlEqualTo("/trainers")).willReturn(response));
    }

    private void givenEmptyTrainersResponse() {
        ResponseDefinitionBuilder response = aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("[]");

        stubFor(get(urlEqualTo("/trainers")).willReturn(response));
    }
}