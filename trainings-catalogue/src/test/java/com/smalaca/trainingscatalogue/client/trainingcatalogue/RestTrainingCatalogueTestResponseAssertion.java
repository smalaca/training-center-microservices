package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import com.smalaca.trainingscatalogue.traningoffer.TrainingOffer;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class RestTrainingCatalogueTestResponseAssertion {
    private final RestTrainingCatalogueTestResponse actual;
    private List<RestTrainingOfferSummaryTestDto> trainingOfferSummaries;
    private List<RestTrainingProgramSummaryTestDto> trainingProgramSummaries;

    private RestTrainingCatalogueTestResponseAssertion(RestTrainingCatalogueTestResponse actual) {
        this.actual = actual;
    }

    public static RestTrainingCatalogueTestResponseAssertion assertThatTrainingCatalogueResponse(RestTrainingCatalogueTestResponse actual) {
        return new RestTrainingCatalogueTestResponseAssertion(actual);
    }

    public RestTrainingCatalogueTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    private RestTrainingCatalogueTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestTrainingCatalogueTestResponseAssertion hasTrainingOfferSummaries(int expected) {
        assertThat(getTrainingOfferSummaries()).hasSize(expected);
        return this;
    }
    
    public RestTrainingCatalogueTestResponseAssertion hasTrainingProgramSummaries(int expected) {
        assertThat(getTrainingProgramSummaries()).hasSize(expected);
        return this;
    }

    public RestTrainingCatalogueTestResponseAssertion containsTrainingOfferSummaryFor(TrainingOffer expected) {
        return containsTrainingOfferSummaryFor(expected, "NO NAME");
    }

    public RestTrainingCatalogueTestResponseAssertion containsTrainingOfferSummaryFor(TrainingOffer expectedTrainingOffer, TrainingProgram expectedTrainingProgram) {
        return containsTrainingOfferSummaryFor(expectedTrainingOffer, expectedTrainingProgram.getName());
    }

    private RestTrainingCatalogueTestResponseAssertion containsTrainingOfferSummaryFor(TrainingOffer expectedTrainingOffer, String expectedName) {
        assertThat(getTrainingOfferSummaries())
                .anySatisfy(summary -> {
                    isSameAsTrainingOfferSummary(summary, expectedTrainingOffer);
                    assertThat(summary.trainingProgramName()).isEqualTo(expectedName);
                });

        return this;
    }
    
    public RestTrainingCatalogueTestResponseAssertion containsTrainingProgramSummaryFor(TrainingProgram expected) {
        assertThat(getTrainingProgramSummaries()).anySatisfy(summary -> isSameAsTrainingProgramSummary(summary, expected));

        return this;
    }

    private List<RestTrainingOfferSummaryTestDto> getTrainingOfferSummaries() {
        if (trainingOfferSummaries == null) {
            trainingOfferSummaries = actual.asTrainingOfferSummaries();
        }

        return trainingOfferSummaries;
    }
    
    private List<RestTrainingProgramSummaryTestDto> getTrainingProgramSummaries() {
        if (trainingProgramSummaries == null) {
            trainingProgramSummaries = actual.asTrainingProgramSummaries();
        }

        return trainingProgramSummaries;
    }

    private void isSameAsTrainingOfferSummary(RestTrainingOfferSummaryTestDto actual, TrainingOffer expected) {
        assertThat(actual.trainingOfferId()).isEqualTo(expected.getTrainingOfferId());
        assertThat(actual.trainerId()).isEqualTo(expected.getTrainerId());
        assertThat(actual.startDate()).isEqualTo(expected.getStartDate());
        assertThat(actual.endDate()).isEqualTo(expected.getEndDate());
    }
    
    private void isSameAsTrainingProgramSummary(RestTrainingProgramSummaryTestDto actual, TrainingProgram expected) {
        assertThat(actual.trainingProgramId()).isEqualTo(expected.getTrainingProgramId());
        assertThat(actual.authorId()).isEqualTo(expected.getAuthorId());
        assertThat(actual.name()).isEqualTo(expected.getName());
    }
}