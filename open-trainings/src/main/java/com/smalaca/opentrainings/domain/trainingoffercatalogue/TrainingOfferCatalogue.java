package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import com.smalaca.architecture.portsandadapters.DrivenPort;

import java.util.UUID;

@DrivenPort
public interface TrainingOfferCatalogue {
    TrainingBookingResponse book(TrainingBookingDto trainingBookingDto);

    TrainingDto detailsOf(UUID trainingId);
}
