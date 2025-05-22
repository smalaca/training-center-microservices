package com.smalaca.trainingmanagement.application.program;

import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.program.TrainingProgramRepository;
import com.smalaca.trainingmanagement.infrastructure.kafka.EventPublisher;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingProgramServiceTest {

    @Mock private TrainingProgramRepository trainingProgramRepository;
    @Mock private EventPublisher eventPublisher;

    private TrainingProgramService service;

    @BeforeEach
    void setUp() {
        service = new TrainingProgramService(trainingProgramRepository, eventPublisher);
    }

    @Test
    void shouldGetAllPrograms() {
        // given
        List<TrainingProgram> programs = List.of(
                createProgram("Java Programming"),
                createProgram("Python Programming")
        );
        given(trainingProgramRepository.findAll()).willReturn(programs);

        // when
        List<TrainingProgram> result = service.getAllPrograms();

        // then
        assertThat(result).isEqualTo(programs);
    }

    @Test
    void shouldGetProgramById() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProgram program = createProgram("Java Programming");
        given(trainingProgramRepository.findById(id)).willReturn(Optional.of(program));

        // when
        TrainingProgram result = service.getProgram(id);

        // then
        assertThat(result).isEqualTo(program);
    }

    @Test
    void shouldThrowExceptionWhenProgramNotFound() {
        // given
        UUID id = UUID.randomUUID();
        given(trainingProgramRepository.findById(id)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> service.getProgram(id));

        // then
        assertThat(thrown)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Training program not found with id: " + id);
    }

    @Test
    void shouldUpdateProgram() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProgram program = createProgram("Java Programming");
        given(trainingProgramRepository.findById(id)).willReturn(Optional.of(program));
        given(trainingProgramRepository.save(program)).willReturn(program);

        String newTitle = "Advanced Java Programming";
        String newDescription = "Learn advanced Java programming";
        String newCategory = "Advanced Programming";
        String newLevel = "Advanced";
        String newTrainer = "Jane Smith";
        int newDurationInDays = 10;

        // when
        TrainingProgram result = service.updateProgram(id, newTitle, newDescription, newCategory, newLevel, newTrainer, newDurationInDays);

        // then
        assertThat(result).isEqualTo(program);
        assertThat(program.getTitle()).isEqualTo(newTitle);
        assertThat(program.getDescription()).isEqualTo(newDescription);
        assertThat(program.getCategory()).isEqualTo(newCategory);
        assertThat(program.getLevel()).isEqualTo(newLevel);
        assertThat(program.getTrainer()).isEqualTo(newTrainer);
        assertThat(program.getDurationInDays()).isEqualTo(newDurationInDays);
        assertThat(program.isActive()).isTrue();

        verify(eventPublisher).publishTrainingProgramUpdated(program);
    }

    @Test
    void shouldWithdrawProgram() {
        // given
        UUID id = UUID.randomUUID();
        TrainingProgram program = createProgram("Java Programming");
        given(trainingProgramRepository.findById(id)).willReturn(Optional.of(program));
        given(trainingProgramRepository.save(program)).willReturn(program);

        String reason = "No longer relevant";

        // when
        service.withdrawProgram(id, reason);

        // then
        assertThat(program.isActive()).isFalse();
        verify(trainingProgramRepository).save(program);
        verify(eventPublisher).publishTrainingProgramWithdrawn(program, reason);
    }

    private TrainingProgram createProgram(String title) {
        return new TrainingProgram(
                UUID.randomUUID(),
                title,
                "Description of " + title,
                "Programming",
                "Beginner",
                "John Doe",
                5
        );
    }
}