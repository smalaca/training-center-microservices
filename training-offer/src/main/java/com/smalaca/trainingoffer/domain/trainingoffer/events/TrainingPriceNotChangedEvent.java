package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;

import java.util.UUID;

@DomainEvent
public record TrainingPriceNotChangedEvent(EventId eventId, UUID offerId, UUID trainingId) implements TrainingOfferEvent {

    public static TrainingPriceNotChangedEvent nextAfter(ConfirmTrainingPriceCommand command) {
        return new TrainingPriceNotChangedEvent(command.commandId().nextEventId(), command.offerId(), command.trainingId());
    }
}