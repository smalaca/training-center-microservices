package com.smalaca.opentrainings.domain.offer;

import java.util.UUID;

public class NoAvailablePlacesException extends RuntimeException {
    private final UUID trainingId;

    NoAvailablePlacesException(UUID trainingId) {
        this.trainingId = trainingId;
    }

    public UUID getTrainingId() {
        return trainingId;
    }
}
