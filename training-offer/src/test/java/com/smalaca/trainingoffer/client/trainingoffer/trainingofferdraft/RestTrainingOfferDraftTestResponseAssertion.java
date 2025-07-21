package com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft;

import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RestTrainingOfferDraftTestResponseAssertion {
    private final RestTrainingOfferDraftTestResponse actual;
    private List<RestTrainingOfferDraftTestDto> trainingOfferDrafts;

    private RestTrainingOfferDraftTestResponseAssertion(RestTrainingOfferDraftTestResponse actual) {
        this.actual = actual;
    }

    public static RestTrainingOfferDraftTestResponseAssertion assertThatTrainingOfferDraftResponse(RestTrainingOfferDraftTestResponse actual) {
        return new RestTrainingOfferDraftTestResponseAssertion(actual);
    }

    public RestTrainingOfferDraftTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
    }

    public RestTrainingOfferDraftTestResponseAssertion isConflict() {
        return hasStatus(CONFLICT);
    }

    public RestTrainingOfferDraftTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    private RestTrainingOfferDraftTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestTrainingOfferDraftTestResponseAssertion hasTrainingOfferDrafts(int expected) {
        assertThat(getTrainingOfferDrafts()).hasSize(expected);
        return this;
    }

    public RestTrainingOfferDraftTestResponseAssertion containsPublishedTrainingOfferDraft(TrainingOfferDraftTestDto expected) {
        return containsTrainingOfferDraft(expected, true);
    }

    public RestTrainingOfferDraftTestResponseAssertion containsUnpublishedTrainingOfferDraft(TrainingOfferDraftTestDto expected) {
        return containsTrainingOfferDraft(expected, false);
    }

    private RestTrainingOfferDraftTestResponseAssertion containsTrainingOfferDraft(TrainingOfferDraftTestDto expected, boolean published) {
        assertThat(getTrainingOfferDrafts()).anySatisfy(draft -> isSameAsTrainingOfferDraft(draft, expected, published));
        return this;
    }

    private List<RestTrainingOfferDraftTestDto> getTrainingOfferDrafts() {
        if (trainingOfferDrafts == null) {
            trainingOfferDrafts = actual.asTrainingOfferDrafts();
        }

        return trainingOfferDrafts;
    }

    public RestTrainingOfferDraftTestResponseAssertion hasPublishedTrainingOfferDraft(TrainingOfferDraftTestDto expected) {
        isSameAsTrainingOfferDraft(actual.asTrainingOfferDraft(), expected, true);
        return this;
    }

    public RestTrainingOfferDraftTestResponseAssertion hasUnpublishedTrainingOfferDraft(TrainingOfferDraftTestDto expected) {
        isSameAsTrainingOfferDraft(actual.asTrainingOfferDraft(), expected, false);
        return this;
    }

    private void isSameAsTrainingOfferDraft(RestTrainingOfferDraftTestDto actual, TrainingOfferDraftTestDto expected, boolean published) {
        assertThat(actual.trainingOfferDraftId()).isEqualTo(expected.getTrainingOfferDraftId());
        assertThat(actual.trainingProgramId()).isEqualTo(expected.getTrainingProgramId());
        assertThat(actual.trainerId()).isEqualTo(expected.getTrainerId());
        assertThat(actual.priceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getPriceAmount());
        assertThat(actual.priceCurrency()).isEqualTo(expected.getPriceCurrency());
        assertThat(actual.minimumParticipants()).isEqualTo(expected.getMinimumParticipants());
        assertThat(actual.maximumParticipants()).isEqualTo(expected.getMaximumParticipants());
        assertThat(actual.startDate()).isEqualTo(expected.getStartDate());
        assertThat(actual.endDate()).isEqualTo(expected.getEndDate());
        assertThat(actual.startTime()).isEqualTo(expected.getStartTime());
        assertThat(actual.endTime()).isEqualTo(expected.getEndTime());
        assertThat(actual.published()).isEqualTo(published);
    }

    public RestTrainingOfferDraftTestResponseAssertion withMessage(String expected) {
        assertThat(actual.asString()).isEqualTo(expected);
        return this;
    }
}