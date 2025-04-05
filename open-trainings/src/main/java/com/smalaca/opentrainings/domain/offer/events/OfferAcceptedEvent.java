package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.OfferAcceptanceSaga;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@DomainEvent
public record OfferAcceptedEvent(
        EventId eventId, UUID offerId, UUID trainingId, UUID participantId,
        BigDecimal trainingPriceAmount, String trainingPriceCurrencyCode,
        BigDecimal finalPriceAmount, String finalPriceCurrencyCode,
        String discountCode, boolean isDiscountCodeUsed, boolean isDiscountCodeAlreadyUsed) implements OfferEvent, OfferAcceptanceSagaEvent {

    public static OfferAcceptedEvent nextAfter(AcceptOfferCommand command, UUID trainingId, Price trainingPrice) {
        return new OfferAcceptedEvent(
                command.commandId().nextEventId(), command.offerId(), trainingId, command.participantId(),
                trainingPrice.amount(), trainingPrice.currencyCode(),
                command.finalPriceAmount(), command.finalPriceCurrency(), command.discountCode(),
                command.isDiscountCodeUsed(), command.isDiscountCodeAlreadyUsed());
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }
}
