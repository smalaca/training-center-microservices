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
        String discountCode) implements OfferEvent, OfferAcceptanceSagaEvent {

    public static OfferAcceptedEvent.Builder offerAcceptedEventBuilder() {
        return new OfferAcceptedEvent.Builder();
    }

    @Override
    public void accept(OfferAcceptanceSaga offerAcceptanceSaga, LocalDateTime consumedAt) {
        offerAcceptanceSaga.accept(this, () -> consumedAt);
    }

    public static class Builder {
        private EventId eventId;
        private UUID offerId;
        private UUID trainingId;
        private UUID participantId;
        private BigDecimal trainingPriceAmount;
        private String trainingPriceCurrencyCode;
        private BigDecimal finalPriceAmount;
        private String finalPriceCurrencyCode;
        private String discountCode;

        public Builder nextAfter(AcceptOfferCommand command) {
            this.eventId = command.commandId().nextEventId();
            return this;
        }

        public Builder withOfferId(UUID offerId) {
            this.offerId = offerId;
            return this;
        }

        public Builder withTrainingId(UUID trainingId) {
            this.trainingId = trainingId;
            return this;
        }

        public Builder withParticipantId(UUID participantId) {
            this.participantId = participantId;
            return this;
        }

        public Builder withTrainingPrice(Price trainingPrice) {
            this.trainingPriceAmount = trainingPrice.amount();
            this.trainingPriceCurrencyCode = trainingPrice.currencyCode();
            return this;
        }

        public Builder withFinalPrice(Price finalPrice) {
            this.finalPriceAmount = finalPrice.amount();
            this.finalPriceCurrencyCode = finalPrice.currencyCode();
            return this;
        }

        public Builder withDiscountCode(String discountCode) {
            this.discountCode = discountCode;
            return this;
        }

        public OfferAcceptedEvent build() {
            return new OfferAcceptedEvent(
                    eventId, offerId, trainingId, participantId, trainingPriceAmount, trainingPriceCurrencyCode,
                    finalPriceAmount, finalPriceCurrencyCode, discountCode);
        }
    }
}
