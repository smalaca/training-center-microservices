package com.smalaca.opentrainings.infrastructure.trainingoffercatalogue;

import com.smalaca.architecture.portsandadapters.DrivenAdapter;
import com.smalaca.opentrainings.domain.price.Price;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingDto;
import com.smalaca.opentrainings.domain.trainingoffercatalogue.TrainingOfferCatalogue;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@DrivenAdapter
public class TrainingOfferCatalogueRestClient implements TrainingOfferCatalogue {

    @Override
    public TrainingDto detailsOf(UUID trainingId) {
        return new TrainingDto(42, Price.of(BigDecimal.valueOf(100), "PLN"));
    }
}
