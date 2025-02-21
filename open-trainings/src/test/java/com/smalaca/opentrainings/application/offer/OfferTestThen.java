package com.smalaca.opentrainings.application.offer;

import com.smalaca.opentrainings.domain.eventregistry.EventRegistry;
import com.smalaca.opentrainings.domain.offer.Offer;
import com.smalaca.opentrainings.domain.offer.OfferAssertion;
import com.smalaca.opentrainings.domain.offer.OfferRepository;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEvent;
import com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEvent;
import com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEventAssertion;
import org.mockito.ArgumentCaptor;

import static com.smalaca.opentrainings.domain.offer.OfferAssertion.assertThatOffer;
import static com.smalaca.opentrainings.domain.offer.events.ExpiredOfferAcceptanceRequestedEventAssertion.assertThatExpiredOfferAcceptanceRequestedEvent;
import static com.smalaca.opentrainings.domain.offer.events.NotAvailableOfferAcceptanceRequestedEventAssertion.assertThatNotAvailableOfferAcceptanceRequestedEvent;
import static com.smalaca.opentrainings.domain.offer.events.OfferAcceptedEventAssertion.assertThatOfferAcceptedEvent;
import static com.smalaca.opentrainings.domain.offer.events.OfferRejectedEventAssertion.assertThatOfferRejectedEvent;
import static com.smalaca.opentrainings.domain.offer.events.UnexpiredOfferAcceptanceRequestedEventAssertion.assertThatUnexpiredOfferAcceptanceRequestedEvent;
import static org.mockito.BDDMockito.then;

class OfferTestThen {
    private final EventRegistry eventRegistry;
    private final OfferRepository offerRepository;

    OfferTestThen(EventRegistry eventRegistry, OfferRepository offerRepository) {
        this.eventRegistry = eventRegistry;
        this.offerRepository = offerRepository;
    }

    OfferAcceptedEventAssertion offerAcceptedEventPublished() {
        ArgumentCaptor<OfferAcceptedEvent> captor = ArgumentCaptor.forClass(OfferAcceptedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OfferAcceptedEvent actual = captor.getValue();

        return assertThatOfferAcceptedEvent(actual);
    }

    OfferRejectedEventAssertion offerRejectedEventPublished() {
        ArgumentCaptor<OfferRejectedEvent> captor = ArgumentCaptor.forClass(OfferRejectedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        OfferRejectedEvent actual = captor.getValue();

        return assertThatOfferRejectedEvent(actual);
    }

    OfferAssertion offerSaved() {
        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        then(offerRepository).should().save(captor.capture());
        Offer actual = captor.getValue();

        return assertThatOffer(actual);
    }

    UnexpiredOfferAcceptanceRequestedEventAssertion unexpiredOfferAcceptanceRequestedEventPublished() {
        ArgumentCaptor<UnexpiredOfferAcceptanceRequestedEvent> captor = ArgumentCaptor.forClass(UnexpiredOfferAcceptanceRequestedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        UnexpiredOfferAcceptanceRequestedEvent actual = captor.getValue();

        return assertThatUnexpiredOfferAcceptanceRequestedEvent(actual);
    }

    ExpiredOfferAcceptanceRequestedEventAssertion expiredOfferAcceptanceRequestedEventPublished() {
        ArgumentCaptor<ExpiredOfferAcceptanceRequestedEvent> captor = ArgumentCaptor.forClass(ExpiredOfferAcceptanceRequestedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        ExpiredOfferAcceptanceRequestedEvent actual = captor.getValue();

        return assertThatExpiredOfferAcceptanceRequestedEvent(actual);
    }

    NotAvailableOfferAcceptanceRequestedEventAssertion notAvailableOfferAcceptanceRequestedEvent() {
        ArgumentCaptor<NotAvailableOfferAcceptanceRequestedEvent> captor = ArgumentCaptor.forClass(NotAvailableOfferAcceptanceRequestedEvent.class);
        then(eventRegistry).should().publish(captor.capture());
        NotAvailableOfferAcceptanceRequestedEvent actual = captor.getValue();

        return assertThatNotAvailableOfferAcceptanceRequestedEvent(actual);
    }
}
