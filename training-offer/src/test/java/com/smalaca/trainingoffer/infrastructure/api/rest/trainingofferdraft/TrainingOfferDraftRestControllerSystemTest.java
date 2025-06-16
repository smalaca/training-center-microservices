package com.smalaca.trainingoffer.infrastructure.api.rest.trainingofferdraft;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingoffer.client.trainingoffer.TrainingOfferTestClient;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.SpringTrainingOfferDraftCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.OK;

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
        given = GivenTrainingOfferDraftFactory.create();
    }

    @AfterEach
    void deleteTrainingOfferDrafts() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springTrainingOfferDraftCrudRepository.deleteAll());
    }

    @Test
    void shouldPublishTrainingOfferDraft() {
        UUID trainingOfferDraftId = existing(given.trainingOfferDraft().initiated());

        MockHttpServletResponse actual = client.trainingOfferDrafts().publish(trainingOfferDraftId);

        assertThat(actual.getStatus()).isEqualTo(OK.value());
    }

    @Test
    void shouldReturnConflictWhenPublishingAlreadyPublishedTrainingOfferDraft() {
        UUID trainingOfferDraftId = existing(given.trainingOfferDraft().published());

        MockHttpServletResponse actual = client.trainingOfferDrafts().publish(trainingOfferDraftId);

        assertThat(actual.getStatus()).isEqualTo(CONFLICT.value());
    }

    private UUID existing(GivenTrainingOfferDraft givenTrainingOfferDraft) {
        TrainingOfferDraft trainingOfferDraft = givenTrainingOfferDraft.getTrainingOfferDraft();
        repository.save(trainingOfferDraft);

        return trainingOfferDraft.trainingOfferDraftId();
    }
}
