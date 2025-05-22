package com.smalaca.trainingportfolio.readmodel;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingProgramReadModelTest {

    @Test
    void shouldCreateTrainingProgramReadModelWithActiveStatus() {
        // given
        UUID id = UUID.randomUUID();
        String title = "Java Programming";
        String description = "Learn Java programming from scratch";
        String category = "Programming";
        String level = "Beginner";
        String trainer = "John Doe";
        int durationInDays = 5;

        // when
        TrainingProgramReadModel readModel = new TrainingProgramReadModel(
                id, title, description, category, level, trainer, durationInDays);

        // then
        assertThat(readModel.getId()).isEqualTo(id);
        assertThat(readModel.getTitle()).isEqualTo(title);
        assertThat(readModel.getDescription()).isEqualTo(description);
        assertThat(readModel.getCategory()).isEqualTo(category);
        assertThat(readModel.getLevel()).isEqualTo(level);
        assertThat(readModel.getTrainer()).isEqualTo(trainer);
        assertThat(readModel.getDurationInDays()).isEqualTo(durationInDays);
        assertThat(readModel.isActive()).isTrue();
        assertThat(readModel.getCreatedAt()).isNotNull();
        assertThat(readModel.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateTrainingProgramReadModel() {
        // given
        TrainingProgramReadModel readModel = new TrainingProgramReadModel(
                UUID.randomUUID(),
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        
        LocalDateTime originalCreatedAt = readModel.getCreatedAt();
        LocalDateTime originalUpdatedAt = readModel.getUpdatedAt();
        
        // Wait a bit to ensure timestamps are different
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        String newTitle = "Advanced Java Programming";
        String newDescription = "Learn advanced Java programming";
        String newCategory = "Advanced Programming";
        String newLevel = "Advanced";
        String newTrainer = "Jane Smith";
        int newDurationInDays = 10;
        boolean newActive = true;

        // when
        readModel.update(newTitle, newDescription, newCategory, newLevel, newTrainer, newDurationInDays, newActive);

        // then
        assertThat(readModel.getTitle()).isEqualTo(newTitle);
        assertThat(readModel.getDescription()).isEqualTo(newDescription);
        assertThat(readModel.getCategory()).isEqualTo(newCategory);
        assertThat(readModel.getLevel()).isEqualTo(newLevel);
        assertThat(readModel.getTrainer()).isEqualTo(newTrainer);
        assertThat(readModel.getDurationInDays()).isEqualTo(newDurationInDays);
        assertThat(readModel.isActive()).isEqualTo(newActive);
        assertThat(readModel.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(readModel.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldWithdrawTrainingProgramReadModel() {
        // given
        TrainingProgramReadModel readModel = new TrainingProgramReadModel(
                UUID.randomUUID(),
                "Java Programming", 
                "Learn Java programming from scratch", 
                "Programming", 
                "Beginner", 
                "John Doe", 
                5);
        
        LocalDateTime originalCreatedAt = readModel.getCreatedAt();
        LocalDateTime originalUpdatedAt = readModel.getUpdatedAt();
        
        // Wait a bit to ensure timestamps are different
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }

        // when
        readModel.withdraw();

        // then
        assertThat(readModel.isActive()).isFalse();
        assertThat(readModel.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(readModel.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
}