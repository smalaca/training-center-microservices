package com.smalaca.opentrainings.domain.personaldatamanagement;

import java.util.UUID;

public record PersonalDataResponse(boolean isSuccessful, UUID participantId) {
    public static PersonalDataResponse successful(UUID participantId) {
        return new PersonalDataResponse(true, participantId);
    }

    public static PersonalDataResponse failed() {
        return new PersonalDataResponse(false, null);
    }

    public boolean isFailed() {
        return !isSuccessful;
    }
}
