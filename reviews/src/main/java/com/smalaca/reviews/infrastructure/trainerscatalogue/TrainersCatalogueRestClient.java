package com.smalaca.reviews.infrastructure.trainerscatalogue;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.reviews.domain.trainerscatalogue.Trainer;
import com.smalaca.reviews.domain.trainerscatalogue.TrainersCatalogue;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@DrivenAdapter
class TrainersCatalogueRestClient implements TrainersCatalogue {
    private final RestClient client;

    TrainersCatalogueRestClient(RestClient client) {
        this.client = client;
    }

    @Override
    public List<Trainer> findAllTrainers() {
        return client
                .get()
                .uri("/trainers")
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
