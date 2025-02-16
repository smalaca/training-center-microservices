package com.smalaca.opentrainings.domain.offer.events;

import com.smalaca.domaindrivendesign.DomainEvent;
import com.smalaca.opentrainings.domain.eventid.EventId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.commands.AcceptOfferCommand;
import com.smalaca.opentrainings.domain.price.Price;

import java.util.UUID;

@DomainEvent
public record OfferAcceptedEvent(
        EventId eventId, UUID offerId, UUID trainingId, UUID participantId,
        Price trainingPrice, Price finalPrice, String discountCode) implements OfferEvent {

    public static OfferAcceptedEvent.Builder offerAcceptedEventBuilder() {
        return new OfferAcceptedEvent.Builder();
    }

    public static class Builder {
        private EventId eventId;
        private UUID offerId;
        private UUID trainingId;
        private UUID participantId;
        private Price trainingPrice;
        private Price finalPrice;
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
            this.trainingPrice = trainingPrice;
            return this;
        }

        public Builder withFinalPrice(Price finalPrice) {
            this.finalPrice = finalPrice;
            return this;
        }

        public Builder withDiscountCode(String discountCode) {
            this.discountCode = discountCode;
            return this;
        }

        public OfferAcceptedEvent build() {
            return new OfferAcceptedEvent(eventId, offerId, trainingId, participantId, trainingPrice, finalPrice, discountCode);
        }
    }
}
