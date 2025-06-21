package com.smalaca.trainingoffer.infrastructure.api.rest.trainingofferdraft;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingoffer.client.trainingoffer.TrainingOfferTestClient;
import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.RestTrainingOfferDraftTestResponse;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.SpringTrainingOfferDraftCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.RestTrainingOfferDraftTestResponseAssertion.assertThatTrainingOfferDraftResponse;

@SystemTest
@Import(TrainingOfferTestClient.class)
class TrainingOfferDraftRestControllerSystemTest {

    @Autowired
    private TrainingOfferDraftRepository repository;

    @Autowired
    private SpringTrainingOfferDraftCrudRepository springTrainingOfferDraftCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TrainingOfferTestClient client;

    private GivenTrainingOfferDraftFactory given;

    @BeforeEach
    void givenTrainingOfferDraftFactory() {
        given = GivenTrainingOfferDraftFactory.create(repository);
    }

    @AfterEach
    void deleteTrainingOfferDrafts() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springTrainingOfferDraftCrudRepository.deleteAll());
    }

    @Test
    void shouldPublishTrainingOfferDraft() {
        TrainingOfferDraftTestDto dto = given.trainingOfferDraft().initiated().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().publish(dto.trainingOfferDraftId());

        assertThatTrainingOfferDraftResponse(actual).isOk();
        assertThatTrainingOfferDraftResponse(client.trainingOfferDrafts().findById(dto.trainingOfferDraftId()))
                .isOk()
                .hasPublishedTrainingOfferDraft(dto);
    }

    @Test
    void shouldReturnConflictWhenPublishingAlreadyPublishedTrainingOfferDraft() {
        UUID trainingOfferDraftId = given.trainingOfferDraft().published().getDto().trainingOfferDraftId();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().publish(trainingOfferDraftId);

        assertThatTrainingOfferDraftResponse(actual)
                .isConflict()
                .withMessage("Training offer draft: " + trainingOfferDraftId + " already published.");
    }

    @Test
    void shouldNotFindNotExistingTrainingOfferDraft() {
        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().findById(UUID.randomUUID());

        assertThatTrainingOfferDraftResponse(actual).notFound();
    }

    @Test
    void shouldFindExistingTrainingOfferDraft() {
        TrainingOfferDraftTestDto dto = given.trainingOfferDraft().initiated().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().findById(dto.trainingOfferDraftId());

        assertThatTrainingOfferDraftResponse(actual)
                .isOk()
                .hasUnpublishedTrainingOfferDraft(dto);
    }

    @Test
    void shouldFindAllTrainingOfferDrafts() {
        TrainingOfferDraftTestDto dtoOne = given.trainingOfferDraft().initiated().getDto();
        TrainingOfferDraftTestDto dtoTwo = given.trainingOfferDraft().published().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().findAll();

        assertThatTrainingOfferDraftResponse(actual)
                .isOk()
                .hasTrainingOfferDrafts(2)
                .containsUnpublishedTrainingOfferDraft(dtoOne)
                .containsPublishedTrainingOfferDraft(dtoTwo);
    }
}
