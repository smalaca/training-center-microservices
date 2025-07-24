package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;

import java.math.BigDecimal;
import java.util.UUID;

@DomainEvent
public record TrainingPriceChangedEvent(EventId eventId, UUID offerId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode) {

    public static TrainingPriceChangedEvent nextAfter(ConfirmTrainingPriceCommand command) {
        return new TrainingPriceChangedEvent(command.commandId().nextEventId(), command.offerId(), command.trainingId(), command.priceAmount(), command.priceCurrencyCode());
    }
}