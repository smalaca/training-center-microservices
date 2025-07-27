package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

public record TrainingDto(UUID trainingId, int availablePlaces, Price price) {
    public boolean hasNoAvailablePlaces() {
        return availablePlaces == 0;
    }
}
