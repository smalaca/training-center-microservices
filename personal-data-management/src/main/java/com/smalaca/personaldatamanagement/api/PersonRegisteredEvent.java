package com.smalaca.personaldatamanagement.api;

import java.util.UUID;

public record PersonRegisteredEvent(EventId eventId, UUID offerId, UUID participantId) {
}
