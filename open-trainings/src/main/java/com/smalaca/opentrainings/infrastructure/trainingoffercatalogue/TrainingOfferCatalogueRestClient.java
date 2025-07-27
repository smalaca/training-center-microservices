package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@DrivenAdapter
public class TrainingOfferCatalogueRestClient implements TrainingOfferCatalogue {
    private final RestClient client;

    TrainingOfferCatalogueRestClient(RestClient client) {
        this.client = client;
    }

    @Override
    public TrainingDto detailsOf(UUID trainingId) {
        RestTrainingOfferDetailDto detail = client
                .get()
                .uri("/trainingoffers/{trainingOfferId}", trainingId)
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(RestTrainingOfferDetailDto.class);

        return new TrainingDto(trainingId, detail.availablePlaces(), Price.of(detail.priceAmount(), detail.priceCurrency()));
    }
}
