package com.smalaca.trainingoffer.domain.trainingoffer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.trainingoffer.domain.eventid.EventId;
import com.smalaca.trainingoffer.domain.price.Price;
import com.smalaca.trainingoffer.domain.trainingoffer.commands.ConfirmTrainingPriceCommand;

import java.math.BigDecimal;
import java.util.UUID;

@DomainEvent
public record TrainingPriceChangedEvent(EventId eventId, UUID offerId, UUID trainingId, BigDecimal priceAmount, String priceCurrencyCode) implements TrainingOfferEvent {

    public static TrainingPriceChangedEvent nextAfter(ConfirmTrainingPriceCommand command, Price price) {
        return new TrainingPriceChangedEvent(command.commandId().nextEventId(), command.offerId(), command.trainingId(), price.amount(), price.currencyCode());
    }
}