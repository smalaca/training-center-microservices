package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.util.UUID;

public record AcceptOfferCommand(CommandId commandId, UUID offerId, UUID participantId, String discountCode, boolean isDiscountAlreadyCodeUsed, boolean isDiscountCodeUsed, BigDecimal priceAmount, String priceCurrency) implements OfferAcceptanceSagaCommand {
    public static AcceptOfferCommand.Builder acceptOfferCommandBuilder(OfferAcceptanceSagaEvent event, UUID participantId) {
        return new AcceptOfferCommand.Builder(event, participantId);
    }

    public static class Builder {
        private final CommandId commandId;
        private final UUID offerId;
        private final UUID participantId;
        private String discountCode;
        private boolean isDiscountAlreadyCodeUsed;
        private boolean isDiscountCodeUsed;
        private BigDecimal priceAmount;
        private String priceCurrency;

        private Builder(OfferAcceptanceSagaEvent event, UUID participantId) {
            commandId = event.eventId().nextCommandId();
            offerId = event.offerId();
            this.participantId = participantId;
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
