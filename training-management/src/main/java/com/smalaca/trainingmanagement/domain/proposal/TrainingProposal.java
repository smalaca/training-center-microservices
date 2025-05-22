package com.smalaca.trainingmanagement.domain.proposal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingProposal {
    @Id
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String level;
    private String trainer;
    private int durationInDays;
    
    @Enumerated(EnumType.STRING)
    private TrainingProposalStatus status;

    public TrainingProposal(String title, String description, String category, String level, String trainer, int durationInDays) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
        this.status = TrainingProposalStatus.PENDING;
    }

    public void update(String title, String description, String category, String level, String trainer, int durationInDays) {
        if (status != TrainingProposalStatus.PENDING) {
            throw new IllegalStateException("Cannot update a proposal that is not in PENDING status");
        }
        
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.trainer = trainer;
        this.durationInDays = durationInDays;
    }

    public UUID accept() {
        if (status != TrainingProposalStatus.PENDING) {
            throw new IllegalStateException("Cannot accept a proposal that is not in PENDING status");
        }
        
        status = TrainingProposalStatus.ACCEPTED;
        return id;
    }

    public void reject() {
        if (status != TrainingProposalStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a proposal that is not in PENDING status");
        }
        
        status = TrainingProposalStatus.REJECTED;
    }
}