package com.smalaca.opentrainings.client.opentrainings.offer;

import com.smalaca.opentrainings.domain.offer.OfferTestDto;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static com.smalaca.opentrainings.domain.offer.OfferNumberAssertion.assertThatOfferNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RestOfferTestResponseAssertion {
    private final RestOfferTestResponse actual;
    private List<RestOfferTestDto> offers;

    private RestOfferTestResponseAssertion(RestOfferTestResponse actual) {
        this.actual = actual;
    }

    public static RestOfferTestResponseAssertion assertThatOfferResponse(RestOfferTestResponse actual) {
        return new RestOfferTestResponseAssertion(actual);
    }

    public RestOfferTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
    }

    public RestOfferTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    private RestOfferTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestOfferTestResponseAssertion hasOffers(int expected) {
        assertThat(getOffers()).hasSize(expected);
        return this;
    }

    public RestOfferTestResponseAssertion containsAcceptedOffer(OfferTestDto expected) {
        return containsOffer(expected, "ACCEPTED");
    }

    public RestOfferTestResponseAssertion containsRejectedOffer(OfferTestDto expected) {
        return containsOffer(expected, "REJECTED");
    }

    public RestOfferTestResponseAssertion containsTerminatedOffer(OfferTestDto expected) {
        return containsOffer(expected, "TERMINATED");
    }

    public RestOfferTestResponseAssertion containsDeclinedOffer(OfferTestDto expected) {
        return containsOffer(expected, "DECLINED");
    }

    public RestOfferTestResponseAssertion containsInitiatedOffer(OfferTestDto expected) {
        return containsOffer(expected, "INITIATED");
    }

    private RestOfferTestResponseAssertion containsOffer(OfferTestDto expected, String expectedStatus) {
        assertThat(getOffers()).anySatisfy(offer -> isSameAsOffer(offer, expected, expectedStatus));
        return this;
    }

    private List<RestOfferTestDto> getOffers() {
        if (offers == null) {
            offers = actual.asOffers();
        }

        return offers;
    }

    public RestOfferTestResponseAssertion hasInitiatedOffer(OfferTestDto expected) {
        isSameAsOffer(actual.asOffer(), expected, "INITIATED");
        return this;
    }

    private void isSameAsOffer(RestOfferTestDto actual, OfferTestDto expected, String expectedStatus) {
        assertThat(actual.offerId()).isEqualTo(expected.getOfferId());
        assertThat(actual.status()).isEqualTo(expectedStatus);
        assertThat(actual.offerId()).isEqualTo(expected.getOfferId());
        assertThat(actual.trainingId()).isEqualTo(expected.getTrainingId());
        assertThatOfferNumber(actual.offerNumber()).isValid();
        assertThat(actual.creationDateTime()).isEqualToIgnoringNanos(expected.getCreationDateTime());
        assertThat(actual.trainingPriceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getTrainingPrice().amount());
        assertThat(actual.trainingPriceCurrency()).isEqualTo(expected.getTrainingPrice().currencyCode());
    }
}
