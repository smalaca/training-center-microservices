package com.smalaca.trainingmanagement.domain.program;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class TrainingProgramTest {

    @Test
    void shouldCreateTrainingProgramWithActiveStatus() {
        // given
        UUID id = UUID.randomUUID();
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;

        // when
        TrainingProgram program = new TrainingProgram(id, title, description, category, level, trainer, durationInDays);

        // then
        assertThat(program.getId()).isEqualTo(id);
        assertThat(program.getTitle()).isEqualTo(title);
        assertThat(program.getDescription()).isEqualTo(description);
        assertThat(program.getCategory()).isEqualTo(category);
        assertThat(program.getLevel()).isEqualTo(level);
        assertThat(program.getTrainer()).isEqualTo(trainer);
        assertThat(program.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(program.isActive()).isTrue();
    }

    @Test
    void shouldUpdateTrainingProgram() {
        // given
        TrainingProgram program = new TrainingProgram(
                UUID.randomUUID(),
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        
        String newTitle = "Advanced Java Programming";
        String newDescription = "Learn advanced Java programming";
        String newCategory = "Advanced Programming";
        String newLevel = "Advanced";
        String newTrainer = "Jane Smith";
        int newDurationInDays = 10;

        // when
        program.update(newTitle, newDescription, newCategory, newLevel, newTrainer, newDurationInDays);

        // then
        assertThat(program.getTitle()).isEqualTo(newTitle);
        assertThat(program.getDescription()).isEqualTo(newDescription);
        assertThat(program.getCategory()).isEqualTo(newCategory);
        assertThat(program.getLevel()).isEqualTo(newLevel);
        assertThat(program.getTrainer()).isEqualTo(newTrainer);
        assertThat(program.getDurationInDays()).isEqualTo(newDurationInDays);
        assertThat(program.isActive()).isTrue();
    }

    @Test
    void shouldWithdrawTrainingProgram() {
        // given
        TrainingProgram program = new TrainingProgram(
                UUID.randomUUID(),
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);

        // when
        program.withdraw();

        // then
        assertThat(program.isActive()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingInactiveProgram() {
        // given
        TrainingProgram program = new TrainingProgram(
                UUID.randomUUID(),
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        program.withdraw();

        // when
        Throwable thrown = catchThrowable(program::withdraw);

        // then
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot withdraw a program that is already inactive");
    }
}