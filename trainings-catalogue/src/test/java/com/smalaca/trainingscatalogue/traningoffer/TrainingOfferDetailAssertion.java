package com.smalaca.trainingscatalogue.traningoffer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingOfferDetailAssertion {
    private final TrainingOfferDetail actual;

    private TrainingOfferDetailAssertion(TrainingOfferDetail actual) {
        this.actual = actual;
    }

    public static TrainingOfferDetailAssertion assertThatTrainingOfferDetail(TrainingOfferDetail actual) {
        return new TrainingOfferDetailAssertion(actual);
    }

    TrainingOfferDetailAssertion hasTrainingOfferId(UUID expected) {
        assertThat(actual.getTrainingOfferId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasTrainerId(UUID expected) {
        assertThat(actual.getTrainerId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasTrainingProgramId(UUID expected) {
        assertThat(actual.getTrainingProgramId()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasStartDate(LocalDate expected) {
        assertThat(actual.getStartDate()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasEndDate(LocalDate expected) {
        assertThat(actual.getEndDate()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasStartTime(LocalTime expected) {
        assertThat(actual.getStartTime()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasEndTime(LocalTime expected) {
        assertThat(actual.getEndTime()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasPriceAmount(BigDecimal expected) {
        assertThat(actual.getPriceAmount()).isEqualByComparingTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasPriceCurrency(String expected) {
        assertThat(actual.getPriceCurrency()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasMinimumParticipants(int expected) {
        assertThat(actual.getMinimumParticipants()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasMaximumParticipants(int expected) {
        assertThat(actual.getMaximumParticipants()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasName(String expected) {
        assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasAgenda(String expected) {
        assertThat(actual.getAgenda()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasPlan(String expected) {
        assertThat(actual.getPlan()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasDescription(String expected) {
        assertThat(actual.getDescription()).isEqualTo(expected);
        return this;
    }

    TrainingOfferDetailAssertion hasNoTrainingProgram() {
        assertThat(actual.getName()).isNull();
        assertThat(actual.getAgenda()).isNull();
        assertThat(actual.getPlan()).isNull();
        assertThat(actual.getDescription()).isNull();
        return this;
    }
}