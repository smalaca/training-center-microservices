package com.smalaca.trainingscatalogue.client.trainingcatalogue;

import com.smalaca.trainingscatalogue.trainingprogram.TrainingProgram;
import com.smalaca.trainingscatalogue.trainingoffer.TrainingOffer;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
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
    
    public RestTrainingCatalogueTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
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
    
    public RestTrainingCatalogueTestResponseAssertion hasTrainingProgram(TrainingProgram expected) {
        RestTrainingProgramTestDto program = actual.asTrainingProgram();
        assertThat(program.trainingProgramId()).isEqualTo(expected.getTrainingProgramId());
        assertThat(program.trainingProgramProposalId()).isEqualTo(expected.getTrainingProgramProposalId());
        assertThat(program.authorId()).isEqualTo(expected.getAuthorId());
        assertThat(program.reviewerId()).isEqualTo(expected.getReviewerId());
        assertThat(program.name()).isEqualTo(expected.getName());
        assertThat(program.agenda()).isEqualTo(expected.getAgenda());
        assertThat(program.plan()).isEqualTo(expected.getPlan());
        assertThat(program.description()).isEqualTo(expected.getDescription());
        assertThat(program.categoriesIds()).containsExactlyInAnyOrderElementsOf(expected.getCategoriesIds());
        
        return this;
    }
    
    public RestTrainingCatalogueTestResponseAssertion hasTrainingOfferDetailWithTrainingProgram(TrainingOffer expectedOffer, TrainingProgram expectedProgram) {
        RestTrainingOfferDetailTestDto detail = actual.asTrainingOfferDetail();

        hasTrainingOfferDetailSameAs(expectedOffer, detail);
        assertThat(detail.name()).isEqualTo(expectedProgram.getName());
        assertThat(detail.agenda()).isEqualTo(expectedProgram.getAgenda());
        assertThat(detail.plan()).isEqualTo(expectedProgram.getPlan());
        assertThat(detail.description()).isEqualTo(expectedProgram.getDescription());
        
        return this;
    }
    
    public RestTrainingCatalogueTestResponseAssertion hasTrainingOfferDetailWithoutTrainingProgram(TrainingOffer expected) {
        RestTrainingOfferDetailTestDto detail = actual.asTrainingOfferDetail();

        hasTrainingOfferDetailSameAs(expected, detail);
        assertThat(detail.name()).isNull();
        assertThat(detail.agenda()).isNull();
        assertThat(detail.plan()).isNull();
        assertThat(detail.description()).isNull();
        
        return this;
    }

    private void hasTrainingOfferDetailSameAs(TrainingOffer expectedOffer, RestTrainingOfferDetailTestDto detail) {
        assertThat(detail.trainingOfferId()).isEqualTo(expectedOffer.getTrainingOfferId());
        assertThat(detail.trainerId()).isEqualTo(expectedOffer.getTrainerId());
        assertThat(detail.trainingProgramId()).isEqualTo(expectedOffer.getTrainingProgramId());
        assertThat(detail.startDate()).isEqualTo(expectedOffer.getStartDate());
        assertThat(detail.endDate()).isEqualTo(expectedOffer.getEndDate());
        assertThat(detail.startTime()).isEqualTo(expectedOffer.getStartTime());
        assertThat(detail.endTime()).isEqualTo(expectedOffer.getEndTime());
        assertThat(detail.priceAmount()).isEqualByComparingTo(expectedOffer.getPriceAmount());
        assertThat(detail.priceCurrency()).isEqualTo(expectedOffer.getPriceCurrency());
        assertThat(detail.minimumParticipants()).isEqualTo(expectedOffer.getMinimumParticipants());
        assertThat(detail.maximumParticipants()).isEqualTo(expectedOffer.getMaximumParticipants());
    }
}