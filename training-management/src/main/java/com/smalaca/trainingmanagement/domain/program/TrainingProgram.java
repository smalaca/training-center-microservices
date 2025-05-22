package com.smalaca.trainingmanagement.domain.program;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingProgram {
    @Id
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String level;
    private String trainer;
    private int durationInDays;
    private boolean active;

    public TrainingProgram(UUID id, String title, String description, String category, String level, String trainer, int durationInDays) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
        this.active = true;
    }

    public void update(String title, String description, String category, String level, String trainer, int durationInDays) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
    }

    public void withdraw() {
        if (!active) {
            throw new IllegalStateException("Cannot withdraw a program that is already inactive");
        }
        
        this.active = false;
    }
}