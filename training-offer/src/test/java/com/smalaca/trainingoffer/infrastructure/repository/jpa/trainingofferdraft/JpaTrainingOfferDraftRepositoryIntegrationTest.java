package com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion.assertThatTrainingOfferDraft;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RepositoryTest
@Import(JpaTrainingOfferDraftRepository.class)
class JpaTrainingOfferDraftRepositoryIntegrationTest {
    @Autowired
    private TrainingOfferDraftRepository repository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final GivenTrainingOfferDraftFactory given = GivenTrainingOfferDraftFactory.create();

    @Test
    void shouldFindNoTrainingOfferDraftWhenDoesNotExist() {
        UUID trainingOfferDraftId = randomUUID();
        Executable executable = () -> repository.findById(trainingOfferDraftId);

        RuntimeException actual = assertThrows(TrainingOfferDraftDoesNotExistException.class, executable);

        assertThat(actual).hasMessage("Training Offer Draft with id " + trainingOfferDraftId + " does not exist.");
    }

    @Test
    void shouldSaveTrainingOfferDraft() {
        GivenTrainingOfferDraft trainingOfferDraft = given.trainingOfferDraft().initiated();

        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(trainingOfferDraft.getTrainingOfferDraft()));

        thenSavedTrainingOfferDraftHasDataEqualTo(trainingOfferDraft.getDto());
    }

    @Test
    void shouldFindTrainingOfferDraftById() {
        GivenTrainingOfferDraft unpublishedDraft = given.trainingOfferDraft().initiated();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(unpublishedDraft.getTrainingOfferDraft()));

        GivenTrainingOfferDraft publishedDraft = given.trainingOfferDraft().published();
        transactionTemplate.executeWithoutResult(transactionStatus -> repository.save(publishedDraft.getTrainingOfferDraft()));

        thenSavedTrainingOfferDraftHasDataEqualTo(unpublishedDraft.getDto()).isNotPublished();
        thenSavedTrainingOfferDraftHasDataEqualTo(publishedDraft.getDto()).isPublished();
    }

    private TrainingOfferDraftAssertion thenSavedTrainingOfferDraftHasDataEqualTo(TrainingOfferDraftTestDto expected) {
        TrainingOfferDraft saved = repository.findById(expected.getTrainingOfferDraftId());

        return assertThatTrainingOfferDraft(saved)
                .hasTrainingOfferDraftId(saved.trainingOfferDraftId())
                .hasTrainingProgramId(expected.getTrainingProgramId())
                .hasTrainerId(expected.getTrainerId())
                .hasPrice(expected.getPrice())
                .hasMinimumParticipants(expected.getMinimumParticipants())
                .hasMaximumParticipants(expected.getMaximumParticipants())
                .hasTrainingSessionPeriod(expected.getTrainingSessionPeriod());
    }

}