package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import com.smalaca.architecture.portsandadapters.DrivenPort;

@DrivenPort
public interface TrainingOfferCatalogue {
    TrainingBookingResponse book(TrainingBookingDto trainingId);
}
