package com.smalaca.trainingportfolio.infrastructure.kafka;

import com.smalaca.schemaregistry.metadata.EventId;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramUpdatedEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProgramWithdrawnEvent;
import com.smalaca.schemaregistry.trainingmanagement.events.TrainingProposalAcceptedEvent;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramReadModel;
import com.smalaca.trainingportfolio.readmodel.TrainingProgramRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingProgramEventConsumerTest {

    @Mock
    private TrainingProgramRepository trainingProgramRepository;

    @InjectMocks
    private TrainingProgramEventConsumer eventConsumer;

    @Test
    void shouldCreateTrainingProgramReadModelWhenTrainingProposalAccepted() {
        // given
        UUID proposalId = UUID.randomUUID();
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;

        TrainingProposalAcceptedEvent event = new TrainingProposalAcceptedEvent(
                EventId.newEventId(),
                proposalId,
                title,
                description,
                category,
                level,
                trainer,
                durationInDays
        );

        // when
        eventConsumer.consumeTrainingProposalAccepted(event);

        // then
        ArgumentCaptor<TrainingProgramReadModel> readModelCaptor = ArgumentCaptor.forClass(TrainingProgramReadModel.class);
        verify(trainingProgramRepository).save(readModelCaptor.capture());

        TrainingProgramReadModel capturedReadModel = readModelCaptor.getValue();
        assertThat(capturedReadModel.getId()).isEqualTo(proposalId);
        assertThat(capturedReadModel.getTitle()).isEqualTo(title);
        assertThat(capturedReadModel.getDescription()).isEqualTo(description);
        assertThat(capturedReadModel.getCategory()).isEqualTo(category);
        assertThat(capturedReadModel.getLevel()).isEqualTo(level);
        assertThat(capturedReadModel.getTrainer()).isEqualTo(trainer);
        assertThat(capturedReadModel.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(capturedReadModel.isActive()).isTrue();
    }

    @Test
    void shouldUpdateTrainingProgramReadModelWhenTrainingProgramUpdated() {
        // given
        UUID programId = UUID.randomUUID();
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;
        boolean active = true;

        TrainingProgramUpdatedEvent event = new TrainingProgramUpdatedEvent(
                EventId.newEventId(),
                programId,
                title,
                description,
                category,
                level,
                trainer,
                durationInDays,
                active
        );

        TrainingProgramReadModel existingReadModel = new TrainingProgramReadModel(
                programId,
                "Old Title",
                "Old Description",
                "Old Category",
                "Old Level",
                "Old Trainer",
                3
        );
        given(trainingProgramRepository.findById(programId)).willReturn(Optional.of(existingReadModel));

        // when
        eventConsumer.consumeTrainingProgramUpdated(event);

        // then
        verify(trainingProgramRepository).save(existingReadModel);
        assertThat(existingReadModel.getTitle()).isEqualTo(title);
        assertThat(existingReadModel.getDescription()).isEqualTo(description);
        assertThat(existingReadModel.getCategory()).isEqualTo(category);
        assertThat(existingReadModel.getLevel()).isEqualTo(level);
        assertThat(existingReadModel.getTrainer()).isEqualTo(trainer);
        assertThat(existingReadModel.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(existingReadModel.isActive()).isEqualTo(active);
    }

    @Test
    void shouldWithdrawTrainingProgramReadModelWhenTrainingProgramWithdrawn() {
        // given
        UUID programId = UUID.randomUUID();
        String reason = "No longer relevant";

        TrainingProgramWithdrawnEvent event = new TrainingProgramWithdrawnEvent(
                EventId.newEventId(),
                programId,
                reason
        );

        TrainingProgramReadModel existingReadModel = new TrainingProgramReadModel(
                programId,
                "Java Programming",
                "Learn Java programming from scratch",
                "Programming",
                "Beginner",
                "John Doe",
                5
        );
        given(trainingProgramRepository.findById(programId)).willReturn(Optional.of(existingReadModel));

        // when
        eventConsumer.consumeTrainingProgramWithdrawn(event);

        // then
        verify(trainingProgramRepository).save(existingReadModel);
        assertThat(existingReadModel.isActive()).isFalse();
    }
}