package com.smalaca.opentrainings.domain.trainingoffercatalogue;

import java.util.UUID;

public record TrainingBookingResponse(boolean isSuccessful, UUID trainingId, UUID participantId) {
    public static TrainingBookingResponse successful(UUID trainingId, UUID participantId) {
        return new TrainingBookingResponse(true, trainingId, participantId);
    }

    public static TrainingBookingResponse failed(UUID trainingId, UUID participantId) {
        return new TrainingBookingResponse(false, trainingId, participantId);
    }

    public boolean isFailed() {
        return !isSuccessful;
    }
}
