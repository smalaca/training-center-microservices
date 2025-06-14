package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion.assertThatTrainingOfferDraft;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingOfferDraftApplicationServiceTest {
    private static final UUID TRAINING_OFFER_DRAFT_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();

    private final TrainingOfferDraftRepository repository = mock(TrainingOfferDraftRepository.class);
    private final TrainingOfferDraftApplicationService service = new TrainingOfferDraftApplicationService(repository);

    @Test
    void shouldMarkTrainingOfferDraftAsPublished() {
        givenExistingTrainingOfferDraft();

        service.publish(TRAINING_OFFER_DRAFT_ID);

        thenTrainingOfferDraftSaved().isPublished();
    }

    private void givenExistingTrainingOfferDraft() {
        TrainingOfferDraft draft = new TrainingOfferDraft(TRAINING_PROGRAM_ID);
        given(repository.findById(TRAINING_OFFER_DRAFT_ID)).willReturn(draft);
    }

    private TrainingOfferDraftAssertion thenTrainingOfferDraftSaved() {
        ArgumentCaptor<TrainingOfferDraft> captor = ArgumentCaptor.forClass(TrainingOfferDraft.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingOfferDraft(captor.getValue());
    }
}
