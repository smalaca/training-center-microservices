package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import com.smalaca.architecture.portsandadapters.DrivenPort;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

@DrivenPort
public interface TrainingOfferCatalogue {
    TrainingBookingResponse book(TrainingBookingDto trainingId);

    Price priceFor(UUID trainingId);
}
