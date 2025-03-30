package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.util.UUID;

public record AcceptOfferCommand(CommandId commandId, UUID offerId, UUID participantId, String discountCode, boolean isDiscountAlreadyCodeUsed, boolean isDiscountCodeUsed, BigDecimal priceAmount, String priceCurrency) implements OfferAcceptanceSagaCommand {
    public static AcceptOfferCommand nextAfter(OfferAcceptanceSagaEvent event, UUID participantId, String discountCode) {
        return new AcceptOfferCommand(event.eventId().nextCommandId(), event.offerId(), participantId, discountCode, false, false, null, null);
    }

    public static AcceptOfferCommand.Builder acceptOfferCommandBuilder() {
        return new AcceptOfferCommand.Builder();
    }

    public static class Builder {
        private CommandId commandId;
        private UUID offerId;
        private UUID participantId;
        private String discountCode;
        private boolean isDiscountAlreadyCodeUsed;
        private boolean isDiscountCodeUsed;
        private BigDecimal priceAmount;
        private String priceCurrency;

        public Builder nextAfter(OfferAcceptanceSagaEvent event) {
            this.commandId = event.eventId().nextCommandId();
            return this;
        }

        public Builder withOfferId(UUID offerId) {
            this.offerId = offerId;
            return this;
        }

        public Builder withParticipantId(UUID participantId) {
            this.participantId = participantId;
            return this;
        }

        public Builder withDiscountCodeAlreadyUsed(String discountCode) {
            this.discountCode = discountCode;
            this.isDiscountAlreadyCodeUsed = true;
            return this;
        }

        public Builder withDiscountCodeUsed(String discountCode) {
            this.discountCode = discountCode;
            this.isDiscountCodeUsed = true;
            return this;
        }

        public Builder withFinalPrice(Price price) {
            this.priceAmount = price.amount();
            this.priceCurrency = price.currencyCode();
            return this;
        }

        public AcceptOfferCommand build() {
            return new AcceptOfferCommand(commandId, offerId, participantId, discountCode, isDiscountAlreadyCodeUsed, isDiscountCodeUsed, priceAmount, priceCurrency);
        }
    }
}
