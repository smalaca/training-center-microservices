package com.smalaca.trainingoffer.infrastructure.api.rest.trainingoffer;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingoffer.client.trainingoffer.TrainingOfferTestClient;
import com.smalaca.trainingoffer.client.trainingoffer.trainingoffer.RescheduleTrainingOfferTestDto;
import com.smalaca.trainingoffer.client.trainingoffer.trainingoffer.RestTrainingOfferTestResponse;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer.SpringTrainingOfferCrudTestRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.SpringTrainingOfferDraftCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingoffer.client.trainingoffer.trainingoffer.RestTrainingOfferTestResponseAssertion.assertThatTrainingOfferResponse;
import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SystemTest
@Import({TrainingOfferTestClient.class, JpaTrainingOfferRepository.class, SpringTrainingOfferCrudTestRepository.class})
class TrainingOfferRestControllerSystemTest {
    private static final LocalDate START_DATE = LocalDate.of(2023, 10, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 10, 5);
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(17, 0);

    @Autowired
    private TrainingOfferDraftRepository trainingOfferDraftRepository;

    @Autowired
    private SpringTrainingOfferDraftCrudRepository springTrainingOfferDraftCrudRepository;

    @Autowired
    private SpringTrainingOfferCrudTestRepository trainingOfferRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private TrainingOfferTestClient client;

    private GivenTrainingOfferDraftFactory given;

    @BeforeEach
    void givenTrainingOfferDraftFactory() {
        given = GivenTrainingOfferDraftFactory.create(trainingOfferDraftRepository);
    }

    @AfterEach
    void deleteTrainingOffers() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springTrainingOfferDraftCrudRepository.deleteAll());
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingOfferRepository.deleteAll());
    }

    @Test
    void shouldRescheduleTrainingOffer() {
        UUID trainingOfferId = givenExistingTrainingOffer();
        RescheduleTrainingOfferTestDto dto = rescheduleTrainingOfferDto(trainingOfferId);

        RestTrainingOfferTestResponse actual = client.trainingOffers().reschedule(dto);

        assertThatTrainingOfferResponse(actual).isOk();
        await().untilAsserted(() -> {
            Optional<TrainingOffer> found = transactionTemplate.execute(status -> trainingOfferRepository.findById(actual.asTrainingOfferId()));
            assertThat(found).isPresent();

            assertThatTrainingOffer(found.get())
                    .hasTrainingOfferId(actual.asTrainingOfferId())
                    .hasTrainingSessionPeriod(dto.startDate(), dto.endDate(), dto.startTime(), dto.endTime());
        });
    }

    private UUID givenExistingTrainingOffer() {
        UUID trainingOfferId = givenPublishedTrainingOfferDraft();
        await().untilAsserted(() -> {
            Optional<TrainingOffer> existingOffer = transactionTemplate.execute(status -> trainingOfferRepository.findById(trainingOfferId));
            assertThat(existingOffer).isPresent();
        });

        return trainingOfferId;
    }

    private UUID givenPublishedTrainingOfferDraft() {
        UUID trainingOfferDraftId = given.trainingOfferDraft().initiated().getDto().getTrainingOfferDraftId();

        return client.trainingOfferDrafts().publish(trainingOfferDraftId).asTrainingOfferId();
    }

    private RescheduleTrainingOfferTestDto rescheduleTrainingOfferDto(UUID trainingOfferId) {
        return new RescheduleTrainingOfferTestDto(trainingOfferId, START_DATE.plusDays(7), END_DATE.plusDays(7), START_TIME, END_TIME);
    }
}