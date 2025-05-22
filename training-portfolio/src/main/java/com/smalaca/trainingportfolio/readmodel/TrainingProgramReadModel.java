package com.smalaca.trainingportfolio.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingProgramReadModel {
    @Id
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String level;
    private String trainer;
    private int durationInDays;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TrainingProgramReadModel(
            UUID id, String title, String description, String category,
            String level, String trainer, int durationInDays) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description, String category, 
                      String level, String trainer, int durationInDays, boolean active) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}