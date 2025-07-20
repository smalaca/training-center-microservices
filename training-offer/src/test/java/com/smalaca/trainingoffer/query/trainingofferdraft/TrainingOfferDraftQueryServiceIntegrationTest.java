package com.smalaca.trainingoffer.query.trainingofferdraft;

import com.smalaca.test.type.RepositoryTest;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.JpaTrainingOfferDraftRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.SpringTrainingOfferDraftCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingoffer.query.trainingofferdraft.TrainingOfferDraftViewAssertion.assertThatTrainingOfferDraft;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@Import({JpaTrainingOfferDraftRepository.class, TrainingOfferDraftQueryService.class})
class TrainingOfferDraftQueryServiceIntegrationTest {
    @Autowired
    private TrainingOfferDraftRepository repository;

    @Autowired
    private TrainingOfferDraftQueryService queryService;

    @Autowired
    private TransactionTemplate transaction;

    @Autowired
    private SpringTrainingOfferDraftCrudRepository springTrainingOfferDraftCrudRepository;

    private GivenTrainingOfferDraftFactory given;

    @BeforeEach
    void init() {
        given = GivenTrainingOfferDraftFactory.create(repository);
    }

    @AfterEach
    void deleteTrainingOfferDrafts() {
        transaction.executeWithoutResult(transactionStatus -> springTrainingOfferDraftCrudRepository.deleteAll());
    }

    @Test
    void shouldFindNoTrainingOfferDraftViewWhenDoesNotExist() {
        UUID trainingOfferDraftId = UUID.randomUUID();

        Optional<TrainingOfferDraftView> actual = queryService.findById(trainingOfferDraftId);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindTrainingOfferDraftViewById() {
        TrainingOfferDraftTestDto dto = transaction.execute(status -> given.trainingOfferDraft().initiated().getDto());

        Optional<TrainingOfferDraftView> actual = queryService.findById(dto.getTrainingOfferDraftId());

        assertThat(actual)
                .isPresent()
                .satisfies(view -> assertThatTrainingOfferDraftHasDataEqualTo(view.get(), dto).isPublished(false));
    }

    @Test
    void shouldFindAllTrainingOfferDrafts() {
        TrainingOfferDraftTestDto dtoOne = transaction.execute(status -> given.trainingOfferDraft().initiated().getDto());
        TrainingOfferDraftTestDto dtoTwo = transaction.execute(status -> given.trainingOfferDraft().published().getDto());

        Iterable<TrainingOfferDraftView> actual = queryService.findAll();

        assertThat(actual).hasSize(2)
                .anySatisfy(view -> assertThatTrainingOfferDraftHasDataEqualTo(view, dtoOne).isPublished(false))
                .anySatisfy(view -> assertThatTrainingOfferDraftHasDataEqualTo(view, dtoTwo).isPublished(true));
    }

    private TrainingOfferDraftViewAssertion assertThatTrainingOfferDraftHasDataEqualTo(TrainingOfferDraftView actual, TrainingOfferDraftTestDto expected) {
        return assertThatTrainingOfferDraft(actual)
                .hasTrainingOfferDraftId(expected.getTrainingOfferDraftId())
                .hasTrainingProgramId(expected.getTrainingProgramId())
                .hasTrainerId(expected.getTrainerId())
                .hasPriceAmount(expected.getPrice().amount())
                .hasPriceCurrency(expected.getPrice().currencyCode())
                .hasMinimumParticipants(expected.getMinimumParticipants())
                .hasMaximumParticipants(expected.getMaximumParticipants())
                .hasStartDate(expected.getTrainingSessionPeriod().startDate())
                .hasEndDate(expected.getTrainingSessionPeriod().endDate())
                .hasStartTime(expected.getTrainingSessionPeriod().startTime())
                .hasEndTime(expected.getTrainingSessionPeriod().endTime());
    }
}
