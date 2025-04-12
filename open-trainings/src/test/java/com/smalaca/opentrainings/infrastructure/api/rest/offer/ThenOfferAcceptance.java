package com.smalaca.opentrainings.infrastructure.api.rest.offer;

import com.smalaca.opentrainings.client.opentrainings.OpenTrainingsTestClient;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDto;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDtoAssertion;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponse;
import com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion;
import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import com.smalaca.opentrainings.query.order.OrderView;
import com.smalaca.opentrainings.query.order.OrderViewAssertion;
import com.smalaca.opentrainings.query.order.OrderViewRepository;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferAcceptanceTestDtoAssertion.assertThatOfferAcceptance;
import static com.smalaca.opentrainings.client.opentrainings.offer.RestOfferTestResponseAssertion.assertThatOfferResponse;
import static com.smalaca.opentrainings.query.order.OrderViewAssertion.assertThatOrder;
import static org.assertj.core.api.Assertions.assertThat;

class ThenOfferAcceptance {
    private final OpenTrainingsTestClient client;
    private final OrderViewRepository orderRepository;
    private RestOfferAcceptanceTestDto actual;

    ThenOfferAcceptance(OpenTrainingsTestClient client, OrderViewRepository orderRepository) {
        this.client = client;
        this.orderRepository = orderRepository;
    }

    ThenOfferAcceptance offerAcceptanceAccepted(UUID offerId) {
        thenOfferAcceptance(offerId)
                .hasOfferId(offerId)
                .isAccepted()
                .isCompleted()
                .hasNoRejectionReason();

        return this;
    }

    ThenOfferAcceptance offerAcceptanceRejected(UUID offerId, String rejectionReason) {
        thenOfferAcceptance(offerId)
                .hasOfferId(offerId)
                .isRejected()
                .isCompleted()
                .hasRejectionReason(rejectionReason);

        return this;
    }

    private RestOfferAcceptanceTestDtoAssertion thenOfferAcceptance(UUID offerId) {
        actual = client.offers().getAcceptanceProgress(offerId);

        return assertThatOfferAcceptance(actual);
    }

    ThenOfferAcceptance offerRejected(OfferTestDto dto) {
        thenOffer()
                .isOk()
                .hasRejectedOffer(dto);

        return this;
    }

    ThenOfferAcceptance offerDeclined(OfferTestDto dto) {
        thenOffer()
                .isOk()
                .hasDeclinedOffer(dto);

        return this;
    }

    ThenOfferAcceptance offerAccepted(OfferTestDto dto) {
        thenOffer()
                .isOk()
                .hasAcceptedOffer(dto);

        return this;
    }

    private RestOfferTestResponseAssertion thenOffer() {
        RestOfferTestResponse offer = client.offers().findById(actual.offerId());

        return assertThatOfferResponse(offer);
    }

    ThenOfferAcceptance orderNotInitiated(OfferTestDto dto) {
        assertThat(orderRepository.findByOfferId(dto.getOfferId())).isEmpty();
        return this;
    }

    ThenOfferAcceptance orderInitiated(OfferTestDto dto) {
        Optional<OrderView> found = orderRepository.findByOfferId(dto.getOfferId());
        assertThatOrderHasDataEqualTo(found.get(), dto);

        return this;
    }

    private OrderViewAssertion assertThatOrderHasDataEqualTo(OrderView order, OfferTestDto dto) {
        return assertThatOrder(order)
                .hasOfferId(dto.getOfferId())
                .hasTrainingId(dto.getTrainingId())
                .hasTrainingPriceAmount(dto.getTrainingPrice().amount())
                .hasTrainingPriceCurrency(dto.getTrainingPrice().currencyCode())
                .hasValidOrderNumber();
    }
}
