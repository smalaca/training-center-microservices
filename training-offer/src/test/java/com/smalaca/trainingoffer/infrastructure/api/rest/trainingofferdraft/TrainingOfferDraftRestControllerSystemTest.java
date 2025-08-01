package com.smalaca.trainingoffer.infrastructure.api.rest.trainingofferdraft;

import com.smalaca.test.type.SystemTest;
import com.smalaca.trainingoffer.client.trainingoffer.TrainingOfferTestClient;
import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.CreateTrainingOfferDraftTestCommand;
import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.RestTrainingOfferDraftTestResponse;
import com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.RestTrainingOfferDraftTestResponseAssertion;
import com.smalaca.trainingoffer.domain.trainingoffer.TrainingOffer;
import com.smalaca.trainingoffer.domain.trainingofferdraft.GivenTrainingOfferDraftFactory;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftTestDto;
import com.smalaca.trainingoffer.infrastructure.api.eventpublisher.kafka.trainingofferdraft.TrainingOfferDraftEventPublisher;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer.JpaTrainingOfferRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingoffer.SpringTrainingOfferCrudTestRepository;
import com.smalaca.trainingoffer.infrastructure.repository.jpa.trainingofferdraft.SpringTrainingOfferDraftCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static com.smalaca.trainingoffer.client.trainingoffer.trainingofferdraft.RestTrainingOfferDraftTestResponseAssertion.assertThatTrainingOfferDraftResponse;
import static com.smalaca.trainingoffer.domain.trainingoffer.TrainingOfferAssertion.assertThatTrainingOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SystemTest
@Import({TrainingOfferTestClient.class, JpaTrainingOfferRepository.class, SpringTrainingOfferCrudTestRepository.class})
class TrainingOfferDraftRestControllerSystemTest {
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();
    private static final UUID TRAINER_ID = UUID.randomUUID();
    private static final BigDecimal PRICE_AMOUNT = BigDecimal.valueOf(1000);
    private static final String PRICE_CURRENCY = "USD";
    private static final int MINIMUM_PARTICIPANTS = 5;
    private static final int MAXIMUM_PARTICIPANTS = 20;
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

    @MockBean
    private TrainingOfferDraftEventPublisher trainingOfferDraftEventPublisher;

    private GivenTrainingOfferDraftFactory given;

    @BeforeEach
    void givenTrainingOfferDraftFactory() {
        given = GivenTrainingOfferDraftFactory.create(trainingOfferDraftRepository);
    }

    @AfterEach
    void deleteTrainingOfferDrafts() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springTrainingOfferDraftCrudRepository.deleteAll());
        transactionTemplate.executeWithoutResult(transactionStatus -> trainingOfferRepository.deleteAll());
    }

    @Test
    void shouldCreateTrainingOfferDraft() {
        CreateTrainingOfferDraftTestCommand command = createTrainingOfferDraftTestCommand();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().create(command);

        assertThatTrainingOfferDraftResponse(actual).isOk();
        thenTrainingOfferDraftCreated(actual)
                .hasUnpublishedTrainingOfferDraft(asTrainingOfferDraftTestDto(actual, command));
    }

    private CreateTrainingOfferDraftTestCommand createTrainingOfferDraftTestCommand() {
        return new CreateTrainingOfferDraftTestCommand(
                TRAINING_PROGRAM_ID, TRAINER_ID, PRICE_AMOUNT, PRICE_CURRENCY,
                MINIMUM_PARTICIPANTS, MAXIMUM_PARTICIPANTS, START_DATE, END_DATE, START_TIME, END_TIME);
    }

    private TrainingOfferDraftTestDto asTrainingOfferDraftTestDto(RestTrainingOfferDraftTestResponse response, CreateTrainingOfferDraftTestCommand command) {
        return new TrainingOfferDraftTestDto.Builder()
                .withTrainingOfferDraftId(response.asTrainingOfferDraftId())
                .withTrainingProgramId(command.trainingProgramId())
                .withTrainerId(command.trainerId())
                .withPrice(command.priceAmount(), command.priceCurrency())
                .withMinimumParticipants(command.minimumParticipants())
                .withMaximumParticipants(command.maximumParticipants())
                .withTrainingSessionPeriod(command.startDate(), command.endDate(), command.startTime(), command.endTime())
                .build();
    }

    private RestTrainingOfferDraftTestResponseAssertion thenTrainingOfferDraftCreated(RestTrainingOfferDraftTestResponse actual) {
        RestTrainingOfferDraftTestResponse found = client.trainingOfferDrafts().findById(actual.asTrainingOfferDraftId());
        return assertThatTrainingOfferDraftResponse(found);
    }

    @Test
    void shouldPublishTrainingOfferDraft() {
        TrainingOfferDraftTestDto dto = given.trainingOfferDraft().initiated().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().publish(dto.getTrainingOfferDraftId());

        assertThatTrainingOfferDraftResponse(actual).isOk();

        await().untilAsserted(() -> {
            assertThatTrainingOfferDraftResponse(client.trainingOfferDrafts().findById(dto.getTrainingOfferDraftId()))
                    .isOk()
                    .hasPublishedTrainingOfferDraft(dto);
        });
    }

    @Test
    void shouldCreateTrainingOfferWhenTrainingOfferDraftIsPublished() {
        TrainingOfferDraftTestDto dto = given.trainingOfferDraft().initiated().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().publish(dto.getTrainingOfferDraftId());

        await().untilAsserted(() -> {
            Optional<TrainingOffer> found = transactionTemplate.execute(status -> trainingOfferRepository.findById(actual.asTrainingOfferId()));
            assertThat(found).isPresent();
            assertThatTrainingOffer(found.get())
                    .hasTrainingOfferId(actual.asTrainingOfferId())
                    .hasTrainingOfferDraftId(dto.getTrainingOfferDraftId())
                    .hasTrainingProgramId(dto.getTrainingProgramId())
                    .hasTrainerId(dto.getTrainerId())
                    .hasPrice(dto.getPriceAmount(), dto.getPriceCurrency())
                    .hasMinimumParticipants(dto.getMinimumParticipants())
                    .hasMaximumParticipants(dto.getMaximumParticipants())
                    .hasNoParticipantsRegistered()
                    .hasTrainingSessionPeriod(dto.getStartDate(), dto.getEndDate(), dto.getStartTime(), dto.getEndTime());
        });
    }

    @Test
    void shouldReturnConflictWhenPublishingAlreadyPublishedTrainingOfferDraft() {
        UUID trainingOfferDraftId = given.trainingOfferDraft().published().getDto().getTrainingOfferDraftId();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().publish(trainingOfferDraftId);

        assertThatTrainingOfferDraftResponse(actual).isConflict();
    }

    @Test
    void shouldNotFindNotExistingTrainingOfferDraft() {
        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().findById(UUID.randomUUID());

        assertThatTrainingOfferDraftResponse(actual).notFound();
    }

    @Test
    void shouldFindExistingTrainingOfferDraft() {
        TrainingOfferDraftTestDto dto = given.trainingOfferDraft().initiated().getDto();

        RestTrainingOfferDraftTestResponse actual = client.trainingOfferDrafts().findById(dto.getTrainingOfferDraftId());

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
