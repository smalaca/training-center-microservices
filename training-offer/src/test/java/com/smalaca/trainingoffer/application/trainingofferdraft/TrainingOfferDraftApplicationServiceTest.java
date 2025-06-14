package com.smalaca.trainingoffer.application.trainingofferdraft;

import com.smalaca.trainingoffer.domain.eventregistry.EventRegistry;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraft;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion;
import com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftRepository;
import com.smalaca.trainingoffer.domain.trainingofferdraft.events.TrainingOfferPublishedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.UUID;

import static com.smalaca.trainingoffer.domain.trainingofferdraft.TrainingOfferDraftAssertion.assertThatTrainingOfferDraft;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TrainingOfferDraftApplicationServiceTest {
    private static final UUID TRAINING_OFFER_DRAFT_ID = UUID.randomUUID();
    private static final UUID TRAINING_PROGRAM_ID = UUID.randomUUID();

    private final TrainingOfferDraftRepository repository = mock(TrainingOfferDraftRepository.class);
    private final EventRegistry eventRegistry = mock(EventRegistry.class);
    private final TrainingOfferDraftApplicationService service = new TrainingOfferDraftApplicationService(repository, eventRegistry);

    @Test
    void shouldMarkTrainingOfferDraftAsPublished() {
        givenExistingTrainingOfferDraft();

        service.publish(TRAINING_OFFER_DRAFT_ID);

        thenTrainingOfferDraftSaved().isPublished();
    }

    @Test
    void shouldPublishTrainingOfferPublishedEventWhenTrainingOfferDraftIsPublished() {
        givenExistingTrainingOfferDraft();

        service.publish(TRAINING_OFFER_DRAFT_ID);

        TrainingOfferPublishedEvent event = thenTrainingOfferPublishedEventPublished();
        assertThat(event.trainingOfferDraftId()).isEqualTo(TRAINING_OFFER_DRAFT_ID);
        assertThat(event.trainingProgramId()).isEqualTo(TRAINING_PROGRAM_ID);
    }

    private void givenExistingTrainingOfferDraft() {
        TrainingOfferDraft draft = new TrainingOfferDraft(TRAINING_PROGRAM_ID);
        assignTrainingOfferDraftId(draft);

        given(repository.findById(TRAINING_OFFER_DRAFT_ID)).willReturn(draft);
    }

    private void assignTrainingOfferDraftId(TrainingOfferDraft trainingOfferDraft) {
        try {
            Field offerIdField = trainingOfferDraft.getClass().getDeclaredField("trainingOfferDraftId");
            offerIdField.setAccessible(true);
            offerIdField.set(trainingOfferDraft, TRAINING_OFFER_DRAFT_ID);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private TrainingOfferDraftAssertion thenTrainingOfferDraftSaved() {
        ArgumentCaptor<TrainingOfferDraft> captor = ArgumentCaptor.forClass(TrainingOfferDraft.class);
        then(repository).should().save(captor.capture());

        return assertThatTrainingOfferDraft(captor.getValue());
    }

    private TrainingOfferPublishedEvent thenTrainingOfferPublishedEventPublished() {
        ArgumentCaptor<TrainingOfferPublishedEvent> captor = ArgumentCaptor.forClass(TrainingOfferPublishedEvent.class);
        then(eventRegistry).should().publish(captor.capture());

        return captor.getValue();
    }
}
