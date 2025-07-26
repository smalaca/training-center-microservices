package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.BookTrainingPlaceCommand;

import java.util.UUID;

@DomainEvent
public record TrainingPlaceBookedEvent(EventId eventId, UUID offerId, UUID participantId, UUID trainingId) implements TrainingOfferEvent {

    public static TrainingPlaceBookedEvent nextAfter(BookTrainingPlaceCommand command) {
        return new TrainingPlaceBookedEvent(command.commandId().nextEventId(), command.offerId(), command.participantId(), command.trainingOfferId());
    }
}