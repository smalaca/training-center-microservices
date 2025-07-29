package com.smalaca.trainingoffer.infrastructure.outbox.jpa;

import com.smalaca.trainingoffer.domain.trainingoffer.events.NoAvailableTrainingPlacesLeftEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingOfferRescheduledEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPlaceBookedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceChangedEvent;
import com.smalaca.trainingoffer.domain.trainingoffer.events.TrainingPriceNotChangedEvent;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxMessageAssertion {
    private final OutboxMessage actual;

    private OutboxMessageAssertion(OutboxMessage actual) {
        this.actual = actual;
    }

    static OutboxMessageAssertion assertThatOutboxMessage(OutboxMessage actual) {
        return new OutboxMessageAssertion(actual);
    }

    OutboxMessageAssertion hasMessageId(UUID expected) {
        assertThat(actual.getMessageId()).isEqualTo(expected);
        return this;
    }

    OutboxMessageAssertion hasOccurredOn(LocalDateTime expected) {
        assertThat(actual.getOccurredOn()).isEqualToIgnoringNanos(expected);
        return this;
    }

    OutboxMessageAssertion hasMessageType(String expected) {
        assertThat(actual.getMessageType()).isEqualTo(expected);
        return this;
    }

    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingOfferPublishedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId())
                .contains("\"trainingOfferDraftId\" : \"" + expected.trainingOfferDraftId())
                .contains("\"trainingProgramId\" : \"" + expected.trainingProgramId())
                .contains("\"trainerId\" : \"" + expected.trainerId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode())
                .contains("\"minimumParticipants\" : " + expected.minimumParticipants())
                .contains("\"maximumParticipants\" : " + expected.maximumParticipants());
        return this;
    }
    
    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPriceChangedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode());
        return this;
    }
    
    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPriceNotChangedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }
    
    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(NoAvailableTrainingPlacesLeftEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }
    
    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingPlaceBookedEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"offerId\" : \"" + expected.offerId())
                .contains("\"participantId\" : \"" + expected.participantId())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId());
        return this;
    }
    
    OutboxMessageAssertion hasPayloadThatContainsAllDataFrom(TrainingOfferRescheduledEvent expected) {
        assertThat(actual.getPayload())
                .contains("\"trainingOfferId\" : \"" + expected.trainingOfferId())
                .contains("\"trainingOfferDraftId\" : \"" + expected.trainingOfferDraftId())
                .contains("\"trainingProgramId\" : \"" + expected.trainingProgramId())
                .contains("\"trainerId\" : \"" + expected.trainerId())
                .contains("\"priceAmount\" : " + expected.priceAmount())
                .contains("\"priceCurrencyCode\" : \"" + expected.priceCurrencyCode())
                .contains("\"minimumParticipants\" : " + expected.minimumParticipants())
                .contains("\"maximumParticipants\" : " + expected.maximumParticipants())
                .contains("\"startDate\" : \"" + expected.startDate())
                .contains("\"endDate\" : \"" + expected.endDate())
                .contains("\"startTime\" : \"" + expected.startTime())
                .contains("\"endTime\" : \"" + expected.endTime())
                .contains("\"rescheduledTrainingOfferId\" : \"" + expected.rescheduledTrainingOfferId());
        return this;
    }
}