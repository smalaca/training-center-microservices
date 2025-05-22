package com.smalaca.trainingmanagement.application.program;

import com.smalaca.trainingmanagement.domain.program.TrainingProgram;
import com.smalaca.trainingmanagement.domain.program.TrainingProgramRepository;
import com.smalaca.trainingmanagement.infrastructure.kafka.EventPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingProgramService {
    private final TrainingProgramRepository trainingProgramRepository;
    private final EventPublisher eventPublisher;

    public List<TrainingProgram> getAllPrograms() {
        return trainingProgramRepository.findAll();
    }

    public TrainingProgram getProgram(UUID id) {
        return trainingProgramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training program not found with id: " + id));
    }

    @Transactional
    public TrainingProgram updateProgram(UUID id, String title, String description, String category, String level, String trainer, int durationInDays) {
        TrainingProgram program = getProgram(id);
        program.update(title, description, category, level, trainer, durationInDays);
        TrainingProgram savedProgram = trainingProgramRepository.save(program);
        
        eventPublisher.publishTrainingProgramUpdated(savedProgram);
        
        return savedProgram;
    }

    @Transactional
    public void withdrawProgram(UUID id, String reason) {
        TrainingProgram program = getProgram(id);
        program.withdraw();
        TrainingProgram savedProgram = trainingProgramRepository.save(program);
        
        eventPublisher.publishTrainingProgramWithdrawn(savedProgram, reason);
    }
}