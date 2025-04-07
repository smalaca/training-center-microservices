package com.smalaca.opentrainings.domain.offeracceptancesaga.commands;

import com.smalaca.opentrainings.domain.commandid.CommandId;
import com.smalaca.opentrainings.domain.offeracceptancesaga.events.OfferAcceptanceSagaEvent;
import com.smalaca.opentrainings.domain.price.Price;

import java.math.BigDecimal;
import java.util.UUID;

public record AcceptOfferCommand(
        CommandId commandId, UUID offerId, UUID participantId, String discountCode,
        boolean isDiscountCodeAlreadyUsed, boolean isDiscountCodeUsed,
        BigDecimal finalPriceAmount, String finalPriceCurrency) implements OfferAcceptanceSagaCommand {
    public static AcceptOfferCommand.Builder acceptOfferCommandBuilder(OfferAcceptanceSagaEvent event, UUID participantId) {
        return new AcceptOfferCommand.Builder(event, participantId);
    }

    public static class Builder {
        private final CommandId commandId;
        private final UUID offerId;
        private final UUID participantId;
        private String discountCode;
        private boolean isDiscountCodeAlreadyUsed;
        private boolean isDiscountCodeUsed;
        private BigDecimal finalPriceAmount;
        private String finalPriceCurrency;

        private Builder(OfferAcceptanceSagaEvent event, UUID participantId) {
            commandId = event.eventId().nextCommandId();
            offerId = event.offerId();
            this.participantId = participantId;
        }

        public Builder withDiscountCodeAlreadyUsed(String discountCode) {
            this.discountCode = discountCode;
            this.isDiscountCodeAlreadyUsed = true;
            return this;
        }

        public Builder withDiscountCodeUsed(String discountCode, Price finalTrainingPrice) {
            this.discountCode = discountCode;
            this.isDiscountCodeUsed = true;
            this.finalPriceAmount = finalTrainingPrice.amount();
            this.finalPriceCurrency = finalTrainingPrice.currencyCode();

            return this;
        }

        public AcceptOfferCommand build() {
            return new AcceptOfferCommand(
                    commandId, offerId, participantId, discountCode, isDiscountCodeAlreadyUsed, isDiscountCodeUsed,
                    finalPriceAmount, finalPriceCurrency);
        }
    }
}
