package com.smalaca.trainingprograms.client.trainingprogram.trainingprogramproposal;

import com.smalaca.trainingprograms.domain.trainingprogramproposal.TrainingProgramProposalTestDto;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RestTrainingProgramProposalTestResponseAssertion {
    private final RestTrainingProgramProposalTestResponse actual;
    private List<RestTrainingProgramProposalTestDto> trainingProgramProposals;

    private RestTrainingProgramProposalTestResponseAssertion(RestTrainingProgramProposalTestResponse actual) {
        this.actual = actual;
    }

    public static RestTrainingProgramProposalTestResponseAssertion assertThatTrainingProgramProposalResponse(RestTrainingProgramProposalTestResponse actual) {
        return new RestTrainingProgramProposalTestResponseAssertion(actual);
    }

    public RestTrainingProgramProposalTestResponseAssertion notFound() {
        return hasStatus(NOT_FOUND);
    }

    public RestTrainingProgramProposalTestResponseAssertion isOk() {
        return hasStatus(OK);
    }

    private RestTrainingProgramProposalTestResponseAssertion hasStatus(HttpStatus expected) {
        assertThat(actual.getStatus()).isEqualTo(expected.value());
        return this;
    }

    public RestTrainingProgramProposalTestResponseAssertion hasTrainingProgramProposals(int expected) {
        assertThat(getTrainingProgramProposals()).hasSize(expected);
        return this;
    }

    public RestTrainingProgramProposalTestResponseAssertion containsTrainingProgramProposal(TrainingProgramProposalTestDto expected) {
        assertThat(getTrainingProgramProposals()).anySatisfy(proposal -> isSameAsTrainingProgramProposal(proposal, expected));
        return this;
    }

    private List<RestTrainingProgramProposalTestDto> getTrainingProgramProposals() {
        if (trainingProgramProposals == null) {
            trainingProgramProposals = actual.asTrainingProgramProposals();
        }

        return trainingProgramProposals;
    }

    public RestTrainingProgramProposalTestResponseAssertion hasTrainingProgramProposal(TrainingProgramProposalTestDto expected) {
        isSameAsTrainingProgramProposal(actual.asTrainingProgramProposal(), expected);
        return this;
    }

    private void isSameAsTrainingProgramProposal(RestTrainingProgramProposalTestDto actual, TrainingProgramProposalTestDto expected) {
        assertThat(actual.trainingProgramProposalId()).isEqualTo(expected.trainingProgramProposalId());
        assertThat(actual.authorId()).isEqualTo(expected.authorId());
        assertThat(actual.name()).isEqualTo(expected.name());
        assertThat(actual.description()).isEqualTo(expected.description());
        assertThat(actual.agenda()).isEqualTo(expected.agenda());
        assertThat(actual.plan()).isEqualTo(expected.plan());
        assertThat(actual.categoriesIds()).containsExactlyInAnyOrderElementsOf(expected.categoriesIds());
        assertThat(actual.status()).isEqualTo(expected.status().name());
        assertThat(actual.reviewerId()).isEqualTo(expected.reviewerId());
    }
}
