package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand;

import java.util.UUID;

@DomainEvent
public record NoAvailableTrainingPlacesLeftEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId) implements TrainingOfferEvent {

    public static NoAvailableTrainingPlacesLeftEvent nextAfter(BookTrainingPlaceCommand command) {
        return new NoAvailableTrainingPlacesLeftEvent(command.commandId().nextEventId(), command.offerId(), command.participantId(), command.trainingId());
    }
}